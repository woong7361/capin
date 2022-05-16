package com.hanghae.finalp.service;

import com.hanghae.finalp.config.security.PrincipalDetails;
import com.hanghae.finalp.entity.Chatroom;
import com.hanghae.finalp.entity.Member;
import com.hanghae.finalp.entity.dto.ChatroomDto;
import com.hanghae.finalp.repository.ChatMemberRepository;
import com.hanghae.finalp.repository.ChatRoomRepository;
import com.hanghae.finalp.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatroomService {

    private final ChatMemberRepository chatMemberRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;


    @Transactional
    public Long createDmChatroom(Long memberId, Long sideMemberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new RuntimeException("not exist member"));
        Member sideMember = memberRepository.findById(sideMemberId).orElseThrow(() -> new RuntimeException("not exist member"));

        Chatroom dm =
                Chatroom.createChatroomByMember(member.getUsername() + "_" + sideMember.getUsername(), member, sideMember);
        chatRoomRepository.save(dm);
        return dm.getId();
    }

    public Slice<ChatroomDto.RoomRes> getChatroomList(Long memberId, Pageable pageable) {
        return chatMemberRepository.findChatroomByMemberId(memberId, pageable).map(ChatroomDto.RoomRes::new);
    }
}
