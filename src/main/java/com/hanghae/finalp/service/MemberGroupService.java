package com.hanghae.finalp.service;

import com.hanghae.finalp.config.exception.customexception.AuthorityException;
import com.hanghae.finalp.config.exception.customexception.EntityNotExistException;
import com.hanghae.finalp.config.exception.customexception.MaxNumberException;
import com.hanghae.finalp.entity.ChatMember;
import com.hanghae.finalp.entity.Chatroom;
import com.hanghae.finalp.entity.Group;
import com.hanghae.finalp.entity.MemberGroup;
import com.hanghae.finalp.entity.mappedsuperclass.Authority;
import com.hanghae.finalp.repository.ChatMemberRepository;
import com.hanghae.finalp.repository.ChatRoomRepository;
import com.hanghae.finalp.repository.GroupRepository;
import com.hanghae.finalp.repository.MemberGroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.hanghae.finalp.config.exception.code.ErrorMessageCode.*;
import static com.hanghae.finalp.config.exception.code.ErrorMessageCode.ENTITY_NOT_FOUND_CODE;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class MemberGroupService {

    private final ChatMemberRepository chatMemberRepository;

    private final MemberGroupRepository memberGroupRepository;

    private final GroupRepository groupRepository;

    private final ChatRoomRepository chatRoomRepository;

    //그룹 참가 신청
    @Transactional
    public void applyGroup(Long memberId, Long groupId) {
        //멤버가 이미 해당 그룹에 속해있는지 확인하기 -> memberGroup에 memberId, groupId 동시에 있는지 확인하면됨
        Optional<MemberGroup> memberGroup = memberGroupRepository.findByMemberIdAndGroupId(memberId, groupId);

        //이미 그룹에 속해있는 경우-> 권한을 확인하기(권한이 반드시 있음)
        if (memberGroup.isPresent()) {
            Authority authority = memberGroup.get().getAuthority(); //get()할 경우 값이 null 이면 exception 반환함.이 경우 괜춘

            if (authority.equals(Authority.OWNER)) {
                throw new AuthorityException(AUTHORITY_ERROR_CODE, "부적절한 접근입니다.");
            } else if (authority.equals(Authority.JOIN)) {
                throw new AuthorityException(AUTHORITY_ERROR_CODE, "카페에 이미 가입중입니다.");
            } else if (authority.equals(Authority.WAIT)){
                throw new AuthorityException(AUTHORITY_ERROR_CODE, "가입 승인을 대기 중입니다.");
            }
        }
        //그룹에 속하지 않은 경우
        //WAIT으로 memberGroup을 생성 -chatroodId는 승인시 따로 넣어줄 예정
        MemberGroup newMemberGroup = MemberGroup.createMemberGroup(Authority.WAIT, memberId, groupId, null);
        Group group= groupRepository.findById(groupId).orElseThrow(
                () -> new EntityNotExistException(ENTITY_NOT_FOUND_CODE, "해당 그룹이 존재하지 않습니다."));
        group.getMemberGroups().add(newMemberGroup);
    }



    //그룹 참가자 승인
    @Transactional
    public void approveGroup(Long myMemberId, Long groupId, Long memberId) {

        //내가 속했으며, 승인을 요청한 멤버그룹을 찾는다
        MemberGroup myMemberGroup = memberGroupRepository.findByMemberIdAndGroupId(myMemberId, groupId)
                .orElseThrow(() -> new EntityNotExistException(ENTITY_NOT_FOUND_CODE, "해당 멤버그룹이 존재하지 않습니다."));

        //그리고 내 auth를 확인 -> 만약 내가 그 멤버그룹의 오너가 아니라면
        if(!Authority.OWNER.equals(myMemberGroup.getAuthority())){
            throw new AuthorityException(AUTHORITY_ERROR_CODE, "승인을 할 수 없는 권한 입니다.");
        }

        //그사람도 같은 멤버그룹에서 대기중인지 확인 & 권한 확인
        MemberGroup yourMemberGroup= memberGroupRepository.findByMemberIdAndGroupId(memberId, groupId)
                .orElseThrow(() -> new EntityNotExistException(ENTITY_NOT_FOUND_CODE, "해당 멤버그룹이 존재하지 않습니다."));

        if(Authority.WAIT.equals(yourMemberGroup.getAuthority())){
            //만약 현재인원이 최대인원보다 작다면
            if(yourMemberGroup.getGroup().getMemberCount() < yourMemberGroup.getGroup().getMaxMemberCount()) {
                yourMemberGroup.setAuthority(Authority.JOIN); //wait일 경우 join으로 바꿔줌
                yourMemberGroup.getGroup().plusMemberCount();

                //승인 전에 안넣어줬던 챗룸아이디를 멤버그룹에 넣어준 후
                yourMemberGroup.setChatroomId(myMemberGroup.getChatroomId());

                //조인이 되는 순간 채팅방도 가입시켜줘야 된다 => 챗멤버 생성필요
                Chatroom chatroom = chatRoomRepository.findById(myMemberGroup.getChatroomId()).orElseThrow(
                        () -> new EntityNotExistException(ENTITY_NOT_FOUND_CODE, "해당 채팅방이 존재하지 않습니다."));

                ChatMember chatMember = ChatMember.createChatMember(yourMemberGroup.getMember(), chatroom);
                chatroom.getChatMembers().add(chatMember);

            }else{
                throw new MaxNumberException(NUMBER_COUNT_ERROR_CODE, "그룹의 최대인원을 초과하였습니다.");
            }
        }

    }

    //그룹 참가자 거절
    @Transactional
    public void denyGroup(Long myMemberId, Long groupId, Long memberId) {

        //내가 속했으며, 승인을 요청한 멤버그룹을 찾는다
        MemberGroup myMemberGroup = memberGroupRepository.findByMemberIdAndGroupId(myMemberId, groupId).orElseThrow(
                () -> new EntityNotExistException(ENTITY_NOT_FOUND_CODE, "해당 멤버그룹이 존재하지 않습니다."));

        //그리고 내 auth를 확인 -> 만약 내가 그 멤버그룹의 오너가 아니라면
        if(!Authority.OWNER.equals(myMemberGroup.getAuthority())){
            throw new AuthorityException(AUTHORITY_ERROR_CODE, "거절을 할 수 없는 권한 입니다.");
        }

        MemberGroup yourMemberGroup= memberGroupRepository.findByMemberIdAndGroupId(memberId, groupId).orElseThrow(
                () -> new EntityNotExistException(ENTITY_NOT_FOUND_CODE, "해당 멤버그룹이 존재하지 않습니다."));

        //그사람의 auth 확인 ->그사람의 권한이 wait일 경우
        if(Authority.WAIT.equals(yourMemberGroup.getAuthority())){
            memberGroupRepository.delete(yourMemberGroup);
            Group group= groupRepository.findById(groupId).orElseThrow(
                    () -> new EntityNotExistException(ENTITY_NOT_FOUND_CODE, "해당 그룹이 존재하지 않습니다."));
            group.getMemberGroups().remove(yourMemberGroup);
        }
    }



    //그룹 참가자 추방
    @Transactional
    public void banGroup(Long myMemberId, Long groupId, Long memberId) {

        //내가 속했으며, 추방할 멤버그룹을 찾는다
        MemberGroup myMemberGroup = memberGroupRepository.findByMemberIdAndGroupId(myMemberId, groupId)
                .orElseThrow(() -> new EntityNotExistException(ENTITY_NOT_FOUND_CODE, "해당 멤버그룹이 존재하지 않습니다."));

        //그리고 내 auth를 확인 -> 만약 내가 그 멤버그룹의 오너가 아니라면
        if (!Authority.OWNER.equals(myMemberGroup.getAuthority())) {
            throw new AuthorityException(AUTHORITY_ERROR_CODE, "거절을 할 수 없는 권한 입니다.");
        }

        //그사람도 같은 멤버그룹에 속했는지 확인
        MemberGroup yourMemberGroup = memberGroupRepository.findByMemberIdAndGroupId(memberId, groupId)
                .orElseThrow(() -> new EntityNotExistException(ENTITY_NOT_FOUND_CODE, "해당 멤버그룹이 존재하지 않습니다."));

        //그사람의 auth 확인 ->그사람의 권한이 join일 경우
        if (Authority.JOIN.equals(yourMemberGroup.getAuthority())) {

            //1.채팅룸에서 드랍 => 1-1.챗멤버를 없애줘야함
            ChatMember chatMember = chatMemberRepository.findByMemberIdAndChatroomId(memberId, yourMemberGroup.getChatroomId())
                    .orElseThrow(() -> new EntityNotExistException(ENTITY_NOT_FOUND_CODE, "해당 챗멤버가 존재하지 않습니다."));
            chatMemberRepository.delete(chatMember);
            //1-2. 채팅룸에서도 챗멤버를 없애 줘야함.
            Chatroom chatroom = chatRoomRepository.findById(yourMemberGroup.getChatroomId())
                    .orElseThrow(() -> new EntityNotExistException(ENTITY_NOT_FOUND_CODE, "해당 채팅방이 존재하지 않습니다."));
            chatroom.getChatMembers().remove(chatMember);

            //2.멤버그룹 삭제
            memberGroupRepository.delete(yourMemberGroup);

            Group group = groupRepository.findById(groupId).orElseThrow(
                    () -> new EntityNotExistException(ENTITY_NOT_FOUND_CODE, "해당 그룹이 존재하지 않습니다."));
            group.getMemberGroups().remove(yourMemberGroup);
            yourMemberGroup.getGroup().minusMemberCount();
        }

    }
}
