package com.hanghae.finalp.repository;

import com.hanghae.finalp.entity.Group;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GroupRepository extends JpaRepository<Group, Long> {

    //페이징
    Page<Group> findAllById(Long Id, Pageable pageable);

    //제목과 지역으로 검색
    Page<Group> findByGroupTitleContaining(String searchKeyword, Pageable pageable);

    @Query("select distinct g from Group g join fetch g.memberGroups where g.memberGroups = :groupId")
    Slice<Group> findMemberByGroupId(@Param("groupId") Long groupId);

}
