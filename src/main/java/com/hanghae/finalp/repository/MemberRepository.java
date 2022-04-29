package com.hanghae.finalp.repository;

import com.hanghae.finalp.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
