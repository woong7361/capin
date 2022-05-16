package com.hanghae.finalp.repository;

import com.hanghae.finalp.entity.ChatMember;
import com.hanghae.finalp.entity.dto.ChatroomDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatMemberRepository extends JpaRepository<ChatMember, Long> {

//    Optional<ChatMember> deleteByMemberId(Long memberId);
    Optional<ChatMember> findByMemberId(Long memberId);

    @Query("select cm from ChatMember cm join fetch cm.chatroom where cm.member.id = :memberId")
    Slice<ChatMember> findChatroomByMemberId(@Param("memberId") Long memberId, Pageable pageable);

}
