package com.hanghae.finalp.repository;

import com.hanghae.finalp.entity.MemberGroup;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberGroupRepository extends JpaRepository<MemberGroup, Long> {


    @Query("select gm from MemberGroup gm join fetch gm.group where gm.member.id = :memberId and not gm.authority = 'WAIT'")
    Slice<MemberGroup> findMyGroupByMemberId(@Param("memberId") Long memberId, Pageable pageable);

    @Query("select gm from MemberGroup gm where gm.group.id = :groupId and not gm.authority = 'WAIT'")
    List<MemberGroup> findJoinMemberByGroupId(@Param("groupId") Long groupId);

    Optional<MemberGroup> findByMemberIdAndGroupId(Long memberId,Long groupId);

    @Query("select gm from MemberGroup gm join fetch gm.group where gm.member.id = :memberId and gm.group.id = :groupId")
    Optional<MemberGroup> findByMemberIdAndGroupIdFetchGroup(@Param("memberId") Long memberId, @Param("groupId") Long groupId);

    @Query("select gm from MemberGroup gm join fetch gm.group where gm.group.id = :groupId")
    List<MemberGroup> findAllByGroupId(@Param("groupId") Long groupId);

    @Query("select gm from MemberGroup gm join fetch gm.group where gm.group.id = :groupId and gm.authority = 'OWNER'")
    Optional<MemberGroup> findGroupOwnerByGroupId(@Param("groupId") Long groupId);
}
