package com.hanghae.finalp.service;

import com.hanghae.finalp.config.exception.customexception.entity.EntityNotExistException;
import com.hanghae.finalp.config.exception.customexception.entity.MemberNotExistException;
import com.hanghae.finalp.entity.ChatMember;
import com.hanghae.finalp.entity.Chatroom;
import com.hanghae.finalp.entity.Member;
import com.hanghae.finalp.entity.dto.ChatroomDto;
import com.hanghae.finalp.entity.mappedsuperclass.RoomType;
import com.hanghae.finalp.repository.ChatMemberRepository;
import com.hanghae.finalp.repository.ChatRoomRepository;
import com.hanghae.finalp.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatroomService {

    private final ChatMemberRepository chatMemberRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;


    /**
     * DM하기 눌렀을 때
     */
    @Transactional
    public Long createDmChatroom(Long memberId, Long sideMemberId) {
        List<ChatMember> chatMembers = chatMemberRepository.findByMemberId(memberId);
        List<Long> chatrooms = chatMembers.stream().map(chatMember -> chatMember.getChatroom().getId()).collect(Collectors.toList());
        Optional<ChatMember> originChatMember = chatMemberRepository.findByInChatroomIdsAndBySideMember(chatrooms, sideMemberId, RoomType.DM);

        //같은 DM 채팅방에 있는 상대편이 존재한다면
        if (originChatMember.isPresent()) {
            return originChatMember.get().getChatroom().getId();
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotExistException());
        Member sideMember = memberRepository.findById(sideMemberId)
                .orElseThrow(() -> new MemberNotExistException());

        Chatroom dm =
                Chatroom.createChatroomByMember(member.getUsername() + "_" + sideMember.getUsername(), member, sideMember);
        chatRoomRepository.save(dm);
        return dm.getId();
    }

    public Slice<ChatroomDto.RoomRes> getChatroomList(Long memberId, Pageable pageable) {
        return chatMemberRepository.findChatroomByMemberId(memberId, pageable).map(ChatroomDto.RoomRes::new);
    }
}
