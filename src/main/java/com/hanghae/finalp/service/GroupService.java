package com.hanghae.finalp.service;

import com.hanghae.finalp.config.exception.customexception.AuthorityException;
import com.hanghae.finalp.config.exception.customexception.EntityNotExistException;
import com.hanghae.finalp.entity.Chatroom;
import com.hanghae.finalp.entity.Group;
import com.hanghae.finalp.entity.Member;
import com.hanghae.finalp.entity.MemberGroup;
import com.hanghae.finalp.entity.dto.GroupDto;
import com.hanghae.finalp.entity.mappedsuperclass.Authority;
import com.hanghae.finalp.repository.ChatRoomRepository;
import com.hanghae.finalp.repository.GroupRepository;
import com.hanghae.finalp.repository.MemberGroupRepository;
import com.hanghae.finalp.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static com.hanghae.finalp.config.exception.code.ErrorMessageCode.AUTHORITY_ERROR_CODE;
import static com.hanghae.finalp.config.exception.code.ErrorMessageCode.ENTITY_NOT_FOUND_CODE;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class GroupService {
    private final MemberGroupRepository memberGroupRepository;
    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;
    private final S3Service s3Service;
    private final ChatRoomRepository chatRoomRepository;


    public Slice<GroupDto.SimpleRes> getMyGroupList(Long memberId, Pageable pageable) {
        Slice<MemberGroup> myGroupByMember = memberGroupRepository.findMyGroupByMemberId(memberId, pageable);
        return myGroupByMember.map(GroupDto.SimpleRes::new);
    }

    @Transactional
    public GroupDto.SimpleRes createGroup(Long memberId, GroupDto.CreateReq createReq, MultipartFile multipartFile) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new EntityNotExistException(ENTITY_NOT_FOUND_CODE, "해당 memberId가 존재하지 않습니다."));
        String imageUrl = s3Service.uploadFile(multipartFile);

        Chatroom groupChatroom = Chatroom.createChatroomByGroup(createReq.getGroupTitle(), member);
        chatRoomRepository.save(groupChatroom);
        Group group = Group.createGroup(createReq, imageUrl, member, groupChatroom.getId());
        groupRepository.save(group);

        return new GroupDto.SimpleRes(group);
    }

    @Transactional
    public void deleteGroup(Long memberId, Long groupId) {
        MemberGroup memberGroup = memberGroupRepository.findByMemberIdAndGroupId(memberId, groupId).orElseThrow(
                () -> new EntityNotExistException(ENTITY_NOT_FOUND_CODE, "해당 멤버그룹이 존재하지 않습니다."));
        if(!memberGroup.getAuthority().equals(Authority.OWNER)){
            throw new AuthorityException(AUTHORITY_ERROR_CODE, "카페를 지울 수 없는 권한 입니다.");
        }

        s3Service.deleteFile(memberGroup.getGroup().getImageUrl());
        groupRepository.deleteById(memberGroup.getGroup().getId());
    }

    @Transactional
    public void patchGroup(Long memberId, Long groupId, GroupDto.CreateReq createReq, MultipartFile multipartFile) {
        MemberGroup memberGroup = memberGroupRepository.findByMemberIdAndGroupId(memberId, groupId).orElseThrow(
                () -> new EntityNotExistException(ENTITY_NOT_FOUND_CODE, "해당 멤버그룹이 존재하지 않습니다."));
        if(!memberGroup.getAuthority().equals(Authority.OWNER)){
            throw new AuthorityException(AUTHORITY_ERROR_CODE, "카페를 지울 수 없는 권한 입니다.");
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


    //------------------------------------------------------------------------------------------------






}