package com.hanghae.finalp.repository;

import com.hanghae.finalp.entity.Notice;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    Slice<Notice> findByMemberId(Long memberId, Pageable pageable);

    @Query("select count(n) from Notice n where n.member.id = :memberId and n.isRead = false")
    long findNonReadCountById(@Param("memberId") Long memberId);
}
