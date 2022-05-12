package com.hanghae.finalp.repository;

import com.hanghae.finalp.entity.MemberGroup;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberGroupRepository extends JpaRepository<MemberGroup, Long> {

    @Query("select gm from MemberGroup gm join fetch gm.group where gm.group.id = :memberId")
    Slice<MemberGroup> findMyGroupByMemberId(@Param("memberId") Long memberId, Pageable pageable);

}
