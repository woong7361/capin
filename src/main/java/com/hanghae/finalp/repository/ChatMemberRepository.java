package com.hanghae.finalp.repository;

import com.hanghae.finalp.entity.ChatMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatMemberRepository extends JpaRepository<ChatMember, Long> {
//    Optional<ChatMember> deleteByMemberId(Long memberId);
    Optional<ChatMember> findByMemberId(Long memberId);
}
