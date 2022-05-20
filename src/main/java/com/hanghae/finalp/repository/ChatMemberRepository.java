package com.hanghae.finalp.repository;

import com.hanghae.finalp.entity.ChatMember;
import com.hanghae.finalp.entity.mappedsuperclass.RoomType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatMemberRepository extends JpaRepository<ChatMember, Long> {

//    Optional<ChatMember> deleteByMemberId(Long memberId);
    List<ChatMember> findByMemberId(Long memberId);
    Optional<ChatMember> findByMemberIdAndChatroomId(Long memberId, Long chatroomid);

    @Query("select cm from ChatMember cm join fetch cm.chatroom where cm.member.id = :memberId")
    Slice<ChatMember> findChatroomByMemberId(@Param("memberId") Long memberId, Pageable pageable);

    @Query("select cm from ChatMember cm join fetch cm.chatroom " +
            "where cm.chatroom.id in :chatrooms and cm.chatroom.roomType = :roomType and cm.member.id = :memberId")
    Optional<ChatMember> findByInChatroomIdsAndBySideMember(
            @Param("chatrooms") List<Long> chatrooms, @Param("memberId") Long memberId, @Param("roomType") RoomType roomType);
}
