package com.hanghae.finalp.service;

import com.hanghae.finalp.entity.*;
import com.hanghae.finalp.entity.dto.GroupDto;
import com.hanghae.finalp.entity.dto.MemberGroupDto;
import com.hanghae.finalp.entity.mappedsuperclass.Authority;
import com.hanghae.finalp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor

public class GroupService {
    private final MemberGroupRepository memberGroupRepository;
    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;
    private final S3Service s3Service;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMemberRepository chatMemberRepository;

    public Slice<GroupDto.SimpleRes> getMyGroupList(Long memberId, Pageable pageable) {
        Slice<MemberGroup> myGroupByMember = memberGroupRepository.findMyGroupByMemberId(memberId, pageable);
        return myGroupByMember.map(GroupDto.SimpleRes::new);
    }

    @Transactional
    public GroupDto.SimpleRes createGroup(Long memberId, GroupDto.CreateReq createReq, MultipartFile multipartFile) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new RuntimeException("not exist member"));
        String imageUrl = s3Service.uploadFile(multipartFile);

        Chatroom groupChatroom = Chatroom.createChatroomByGroup(createReq.getGroupTitle(), member);
        chatRoomRepository.save(groupChatroom);
        Group group = Group.createGroup(createReq, imageUrl, member, groupChatroom.getId());
        groupRepository.save(group);

        return new GroupDto.SimpleRes(group);
    }

    @Transactional
    public void deleteGroup(Long memberId, Long groupId) {
        MemberGroup memberGroup = memberGroupRepository.findByMemberIdAndGroupId(memberId, groupId)
                .orElseThrow(() -> new RuntimeException("not exist member"));
        if(!memberGroup.getAuthority().equals(Authority.OWNER)){
            throw new RuntimeException("not owner");
        }

        s3Service.deleteFile(memberGroup.getGroup().getImageUrl());
        groupRepository.deleteById(memberGroup.getGroup().getId());
    }

    @Transactional
    public void patchGroup(Long memberId, Long groupId, GroupDto.CreateReq createReq, MultipartFile multipartFile) {
        MemberGroup memberGroup = memberGroupRepository.findByMemberIdAndGroupId(memberId, groupId)
                .orElseThrow(() -> new RuntimeException("not owner or not exist"));
        if(!memberGroup.getAuthority().equals(Authority.OWNER)){
            throw new RuntimeException("not owner");
        }
        //fetch join 필요
        Group group = memberGroup.getGroup();

        s3Service.deleteFile(group.getImageUrl());
        String imageUrl = s3Service.uploadFile(multipartFile);

        group.patch(createReq, imageUrl);
    }




    //------------------------------------------------------------------------------------

    //페이징
    @Transactional
    public Page<Group> getGroupList(Long groupId, Pageable pageable) {
        return groupRepository.findAllById(groupId, pageable);
    }

    //그룹 검색
    @Transactional
    public Page<Group> groupSearch(String searchKeyword, Pageable pageable) {
        return groupRepository.findByGroupTitleContaining(searchKeyword, pageable);
    }

    //특정 그룹 불러오기
    @Transactional
    public Slice<Group> groupView(Long groupId){
        return groupRepository.findMemberByGroupId(groupId);
    }

    //그룹 참가 신청
    @Transactional
    public void applyGroup(Long memberId, Long groupId) {
        //멤버가 이미 해당 그룹에 속해있는지 확인하기 -> memberGroup에 memberId, groupId 동시에 있는지 확인하면됨
        Optional<MemberGroup> memberGroup = memberGroupRepository.findByMemberIdAndGroupId(memberId, groupId);

        //이미 그룹에 속해있는 경우-> 권한을 확인하기(권한이 반드시 있음)
        if (memberGroup.isPresent()) {
            Authority authority = memberGroup.get().getAuthority(); //get()할 경우 값이 null 이면 exception 반환함.이 경우 괜춘

            if (authority.equals(Authority.OWNER)) {
                throw new RuntimeException("부적절한 접근입니다.");
            } else if (authority.equals(Authority.JOIN)) {
                throw new RuntimeException("이미 가입중입니다.");
            } else if (authority.equals(Authority.WAIT)){
                throw new RuntimeException("가입 승인을 대기 중입니다.");
            }
        }
        //그룹에 속하지 않은 경우
        Member member= memberRepository.findById(memberId).orElseThrow(
                () -> new IllegalArgumentException("해당 memberId가 존재하지 않습니다."));
        Group group= groupRepository.findById(groupId).orElseThrow(
                () -> new IllegalArgumentException("해당 그룹이 존재하지 않습니다."));
        //WAIT으로 memberGroup을 생성 -chatroodId는 승인시 따로 넣어줄 예정
        MemberGroup newMemberGroup = MemberGroup.createMemberGroup(Authority.WAIT, member, group, null);

        group.getMemberGroups().add(newMemberGroup);
    }



    //그룹 참가자 승인
    @Transactional
    public void approveGroup(Long myMemberId, Long groupId, Long memberId) {

        //내가 속했으며, 승인을 요청한 멤버그룹을 찾는다
        MemberGroup myMemberGroup = memberGroupRepository.findByMemberIdAndGroupId(myMemberId, groupId)
                .orElseThrow(() -> new IllegalArgumentException("해당 그룹이 존재하지 않습니다."));

        //그리고 내 auth를 확인 -> 만약 내가 그 멤버그룹의 오너이면
        if(Authority.OWNER.equals(myMemberGroup.getAuthority())){
            //그사람도 같은 멤버그룹에서 대기중인지 확인
            MemberGroup yourMemberGroup= memberGroupRepository.findByMemberIdAndGroupId(memberId, groupId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 그룹이 존재하지 않습니다."));

            if(Authority.WAIT.equals(yourMemberGroup.getAuthority())){
                //만약 현재인원이 최대인원보다 작다면
                if(yourMemberGroup.getGroup().getMemberCount() < yourMemberGroup.getGroup().getMaxMemberCount()) {
                    yourMemberGroup.setAuthority(Authority.JOIN); //wait일 경우 join으로 바꿔줌
                    yourMemberGroup.getGroup().addMemberCount();

                    //승인 전에 안넣어줬던 챗룸아이디를 멤버그룹에 넣어준 후
                    yourMemberGroup.setChatroomId(myMemberGroup.getChatroomId());

                    //조인이 되는 순간 채팅방도 가입시켜줘야 된다 => 챗멤버 생성필요
                    Chatroom chatroom = chatRoomRepository.findById(myMemberGroup.getChatroomId())
                            .orElseThrow(() -> new IllegalArgumentException("해당 채팅방이 존재하지 않습니다."));

                    ChatMember chatMember = ChatMember.createChatMember(yourMemberGroup.getMember(), chatroom);
                    chatroom.getChatMembers().add(chatMember);

                }else{
                    throw new RuntimeException("최대인원 초과");
                }
            }
        }
    }

    //그룹 참가자 거절
    @Transactional
    public void denyGroup(Long myMemberId, Long groupId, Long memberId) {

        //내가 속했으며, 승인을 요청한 멤버그룹을 찾는다
        MemberGroup myMemberGroup = memberGroupRepository.findByMemberIdAndGroupId(myMemberId, groupId)
                .orElseThrow(() -> new IllegalArgumentException("해당 그룹이 존재하지 않습니다."));

        //그리고 내 auth를 확인 -> 만약 내가 그 멤버그룹의 오너이면
        if(Authority.OWNER.equals(myMemberGroup.getAuthority())){

            MemberGroup yourMemberGroup= memberGroupRepository.findByMemberIdAndGroupId(memberId, groupId).orElseThrow(
                    () -> new IllegalArgumentException("해당 그룹이 존재하지 않습니다."));

            //그사람의 auth 확인 ->그사람의 권한이 wait일 경우
            if(Authority.WAIT.equals(yourMemberGroup.getAuthority())){
                memberGroupRepository.delete(yourMemberGroup);
                Group group= groupRepository.findById(groupId).orElseThrow(
                        () -> new IllegalArgumentException("해당 그룹이 존재하지 않습니다."));
                group.getMemberGroups().remove(yourMemberGroup);
            }
        }
    }



    //그룹 참가자 추방
    @Transactional
    public void banGroup(Long myMemberId, Long groupId, Long memberId) {

        //내가 속했으며, 추방할 멤버그룹을 찾는다
        MemberGroup myMemberGroup = memberGroupRepository.findByMemberIdAndGroupId(myMemberId, groupId)
                .orElseThrow(() -> new IllegalArgumentException("해당 그룹이 존재하지 않습니다."));

        //그리고 내 auth를 확인 -> 만약 내가 그 멤버그룹의 오너이면
        if(Authority.OWNER.equals(myMemberGroup.getAuthority())){

            //그사람도 같은 멤버그룹에 속했는지 확인
            MemberGroup yourMemberGroup= memberGroupRepository.findByMemberIdAndGroupId(memberId, groupId).orElseThrow(
                    () -> new IllegalArgumentException("해당 그룹이 존재하지 않습니다."));

            //그사람의 auth 확인 ->그사람의 권한이 join일 경우
            if(Authority.JOIN.equals(yourMemberGroup.getAuthority())){
//                yourMemberGroup.setAuthority(null);

                //멤버그룹 삭제
                yourMemberGroup.getGroup().minusMemberCount();
                memberGroupRepository.delete(yourMemberGroup);

                Group group= groupRepository.findById(groupId).orElseThrow(
                        () -> new IllegalArgumentException("해당 그룹이 존재하지 않습니다."));
                group.getMemberGroups().remove(yourMemberGroup);

                //채팅룸에서도 드랍 =>
                // 1.챗멤버를 없애줘야함
                ChatMember chatMember = chatMemberRepository.findByMemberId(memberId)
                        .orElseThrow(() -> new IllegalArgumentException("해당 채팅방이 존재하지 않습니다."));
                chatMemberRepository.delete(chatMember);

                //2. 채팅룸에서도 챗멤버를 없애 줘야함. 
                Chatroom chatroom = chatRoomRepository.findById(myMemberGroup.getChatroomId())
                        .orElseThrow(() -> new IllegalArgumentException("해당 채팅방이 존재하지 않습니다."));
                chatroom.getChatMembers().remove(chatMember);
            }
        }
    }

    //------------------------------------------------------------------------------------------------


    @Transactional
    public void setlocation(Long memberId, Long groupId, MemberGroupDto.Request request) { //wait체크 추가
        //해당하는 멤버그룹에 받아온 값을 넣어준다
        MemberGroup memberGroup = memberGroupRepository.findByMemberIdAndGroupId(memberId, groupId)
                .orElseThrow(() -> new IllegalArgumentException("해당 그룹이 존재하지 않습니다."));

        if(Authority.WAIT.equals(memberGroup.getAuthority())){
            throw new RuntimeException("가입 승인이 완료되지 않았습니다.");
        }

        memberGroup.setLocation(request.getStartLocationX(), request.getStartLocationY(), request.getStartAddress());
    }


    @Transactional
    public MemberGroupDto.Response recommendLocation(Long groupId) {
        //그룹에 속해있는 멤버들을 다 찾는다. => 멤버그룹에서 그룹아이디를 가진 멤버그룹을 다 찾음
        List<MemberGroup> memberGroupList = memberGroupRepository.findAllByGroupId(groupId);

        //멤버들의 locationx,y를 다 받아와서 평균값을 반환함
        Double totalX = 0.0;
        Double totalY = 0.0;
        for (MemberGroup memberGroup : memberGroupList){
            Double startLocationX = Double.parseDouble(memberGroup.getStartLocationX());
            Double startLocationY = Double.parseDouble(memberGroup.getStartLocationY());

            totalX += startLocationX;
            totalY += startLocationY;
        }

        String averageX = Double.toString(totalX / memberGroupList.size());
        String averageY = Double.toString(totalY / memberGroupList.size());

        MemberGroupDto.Response response = new MemberGroupDto.Response();
        response.setStartLocationX(averageX);
        response.setStartLocationY(averageY);

        return response;
    }
}
