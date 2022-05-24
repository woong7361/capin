package com.hanghae.finalp.repository;

import com.hanghae.finalp.entity.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {

    //제목과 지역으로 검색
    @Query("select g from Group g where g.roughAddress like CONCAT('%',:searchKeyword,'%') or g.groupTitle like CONCAT('%',:searchKeyword,'%')")
    Page<Group> findAllByGroupTitleContainingOrRoughAddressContaining(@Param("searchKeyword") String searchKeyword, Pageable pageable);


    @Query("select distinct g from Group g join fetch g.memberGroups where g.id = :groupId")
    List<Group> findMemberByGroupId(@Param("groupId") Long groupId);


}
