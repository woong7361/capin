package com.hanghae.finalp.service;

import com.hanghae.finalp.config.exception.customexception.authority.AuthorJoinException;
import com.hanghae.finalp.config.exception.customexception.authority.AuthorOwnerException;
import com.hanghae.finalp.config.exception.customexception.authority.AuthorWaitException;
import com.hanghae.finalp.config.exception.customexception.authority.AuthorityException;
import com.hanghae.finalp.config.exception.customexception.entity.EntityNotExistException;
import com.hanghae.finalp.config.exception.customexception.entity.MemberGroupNotExistException;
import com.hanghae.finalp.config.exception.customexception.etc.DuplicationRequestException;
import com.hanghae.finalp.config.exception.customexception.etc.MaxNumberException;
import com.hanghae.finalp.entity.ChatMember;
import com.hanghae.finalp.entity.Chatroom;
import com.hanghae.finalp.entity.MemberGroup;
import com.hanghae.finalp.entity.Notice;
import com.hanghae.finalp.entity.dto.MemberGroupDto;
import com.hanghae.finalp.entity.mappedsuperclass.Authority;
import com.hanghae.finalp.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class MemberGroupService {

    private final ChatMemberRepository chatMemberRepository;
    private final MemberGroupRepository memberGroupRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final NoticeRepository noticeRepository;

    /**
     * 그룹 참가 신청
     */
    @Transactional
    public void applyGroup(Long memberId, String username, Long groupId) {
        log.debug("custom log:: 이미 신청하거나 가입되어있는지 확인");
        //중복된 요청입니다
        memberGroupRepository.findByMemberIdAndGroupId(memberId, groupId)
                .ifPresent((mg) -> {throw new DuplicationRequestException("apply group");});

        //WAIT으로 memberGroup을 생성 -chatroodId는 승인시 따로 넣어줄 예정
        MemberGroup memberGroup = MemberGroup.createMemberGroup(Authority.WAIT, memberId, groupId, null);
        memberGroupRepository.save(memberGroup);

        log.debug("custom log:: create notice for group owner");
        MemberGroup ownerMemberGroup = memberGroupRepository.findGroupOwnerByGroupId(groupId)
                .orElseThrow(MemberGroupNotExistException::new);

        Notice notice = Notice.createGroupApplyNotice(ownerMemberGroup.getGroup().getGroupTitle(), username, ownerMemberGroup.getMember());
        noticeRepository.save(notice);
    }

    /**
     * 그룹 참가자 승인
     */
    @Transactional
    public void approveGroup(Long ownerMemberId, Long groupId, Long targetMemberId) {

        log.debug("custom log:: owner's memberGroup 확인");
        MemberGroup ownerMemberGroup = memberGroupRepository.findByMemberIdAndGroupId(ownerMemberId, groupId)
                .orElseThrow(MemberGroupNotExistException::new);

        if(!Authority.OWNER.equals(ownerMemberGroup.getAuthority())) throw new AuthorOwnerException();

        log.debug("custom log:: target's memberGroup 확인");
        MemberGroup targetMemberGroup = memberGroupRepository.findByMemberIdAndGroupId(targetMemberId, groupId)
                .orElseThrow(MemberGroupNotExistException::new);

        if(!Authority.WAIT.equals(targetMemberGroup.getAuthority())) throw new AuthorWaitException();

        log.debug("custom log:: 그룹의 최대 인원수 확인");
        if (!(targetMemberGroup.getGroup().getMemberCount() < targetMemberGroup.getGroup().getMaxMemberCount())) {
            throw new MaxNumberException();
        }

        targetMemberGroup.joinGroup(); //wait일 경우 join으로 바꿔줌
        targetMemberGroup.getGroup().plusMemberCount();

        log.debug("custom log:: chatroom 관련 logic");
        //승인 전에 안넣어줬던 챗룸아이디를 멤버그룹에 넣어준 후
        targetMemberGroup.joinGroupChatRoom(ownerMemberGroup.getChatroomId());
        //조인이 되는 순간 채팅방도 가입시켜줘야 된다 => 챗멤버 생성필요
        Chatroom chatroom = chatRoomRepository.findById(ownerMemberGroup.getChatroomId()).orElseThrow(
                EntityNotExistException::new);
        ChatMember chatMember = ChatMember.createChatMember(targetMemberGroup.getMember(), chatroom);
        chatroom.getChatMembers().add(chatMember);

        log.debug("custom log:: create notice for target");
        Notice notice = Notice.createGroupApproveNotice(targetMemberGroup.getGroup().getGroupTitle(), targetMemberGroup.getMember());
        noticeRepository.save(notice);
    }

    /**
     * 그룹 참가자 거절
     */
    @Transactional
    public void denyGroup(Long ownerMemberId, Long groupId, Long targetMemberId) {

        log.debug("custom log:: owner's memberGroup 확인");
        MemberGroup ownerMemberGroup = memberGroupRepository.findByMemberIdAndGroupId(ownerMemberId, groupId).orElseThrow(
                MemberGroupNotExistException::new);

        if(!Authority.OWNER.equals(ownerMemberGroup.getAuthority())) throw new AuthorOwnerException();

        log.debug("custom log:: target's memberGroup 확인");
        MemberGroup targetMemberGroup= memberGroupRepository.findByMemberIdAndGroupId(targetMemberId, groupId).orElseThrow(
                MemberGroupNotExistException::new);

        if(!Authority.WAIT.equals(targetMemberGroup.getAuthority())) throw new AuthorWaitException();

        log.debug("custom log:: create notice for target");
        Notice notice = Notice.createGroupDenyNotice(targetMemberGroup.getGroup().getGroupTitle(), targetMemberGroup.getMember());
        noticeRepository.save(notice);

        memberGroupRepository.delete(targetMemberGroup);
    }

    /**
     * 그룹 참가자 추방
     */
    @Transactional
    public void banGroup(Long ownerMemberId, Long groupId, Long targetMemberId) {
        log.debug("custom log:: owner's memberGroup 확인");
        MemberGroup ownerMemberGroup = memberGroupRepository.findByMemberIdAndGroupId(ownerMemberId, groupId)
                .orElseThrow(MemberGroupNotExistException::new);

        if (!Authority.OWNER.equals(ownerMemberGroup.getAuthority())) throw new AuthorOwnerException();

        log.debug("custom log:: target's memberGroup 확인");
        MemberGroup targetMemberGroup = memberGroupRepository.findByMemberIdAndGroupId(targetMemberId, groupId)
                .orElseThrow(MemberGroupNotExistException::new);

        if (!Authority.JOIN.equals(targetMemberGroup.getAuthority())) throw new AuthorJoinException();

        log.debug("custom log:: chatroom 관련 logic");
        //1.채팅룸에서 드랍 => 1-1.챗멤버를 없애줘야함
        ChatMember chatMember = chatMemberRepository.findByMemberIdAndChatroomId(targetMemberId, targetMemberGroup.getChatroomId())
                .orElseThrow(EntityNotExistException::new);
        chatMemberRepository.delete(chatMember);
        //1-2. 채팅룸에서도 챗멤버를 없애 줘야함.
        Chatroom chatroom = chatRoomRepository.findById(targetMemberGroup.getChatroomId())
                .orElseThrow(EntityNotExistException::new);
        chatroom.getChatMembers().remove(chatMember);

        log.debug("custom log:: create notice for target");
        Notice notice = Notice.createGroupBanNotice(targetMemberGroup.getGroup().getGroupTitle(), targetMemberGroup.getMember());
        noticeRepository.save(notice);

        //2.멤버그룹 삭제
        targetMemberGroup.getGroup().minusMemberCount();
        memberGroupRepository.delete(targetMemberGroup);

    }


    /**
     * 그룹 참가 취소
     */
    @Transactional
    public void cancelApplyGroup(Long memberId, Long groupId) {

        log.debug("custom log:: target's memberGroup 확인");
        MemberGroup memberGroup = memberGroupRepository.findByMemberIdAndGroupId(memberId, groupId)
                .orElseThrow(MemberGroupNotExistException::new);

        if (!memberGroup.getAuthority().equals(Authority.WAIT)) {
            throw new AuthorWaitException();
        }

        memberGroupRepository.delete(memberGroup);
    }

    /**
     * 그룹 나가기
     */
    @Transactional
    public void exitGroup(Long memberId, Long groupId) {
        log.debug("custom log:: target's memberGroup 확인");
        MemberGroup memberGroup = memberGroupRepository.findByMemberIdAndGroupId(memberId, groupId)
                .orElseThrow(MemberGroupNotExistException::new);

        if (!memberGroup.getAuthority().equals(Authority.JOIN)) throw new AuthorJoinException();

        log.debug("custom log:: chatroom 관련 logic");
        ChatMember chatMember = chatMemberRepository.findByMemberIdAndChatroomId(memberId, memberGroup.getChatroomId())
                .orElseThrow(EntityNotExistException::new);
        chatMemberRepository.delete(chatMember);
        memberGroup.getGroup().minusMemberCount();

        memberGroupRepository.delete(memberGroup);
    }


    /**
     * 개인의 세부 주소 작성
     */
    @Transactional
    public void setlocation(Long memberId, Long groupId, MemberGroupDto.LocationReq locationReq) {
        //해당하는 멤버그룹에 받아온 값을 넣어준다
        MemberGroup memberGroup = memberGroupRepository.findByMemberIdAndGroupId(memberId, groupId)
                .orElseThrow(() -> new MemberGroupNotExistException());

        if (memberGroup.getAuthority().equals(Authority.WAIT)) throw new AuthorityException();

        memberGroup.setStartLocation(locationReq.getStartLocationX(), locationReq.getStartLocationY(), locationReq.getStartAddress());
    }


}
