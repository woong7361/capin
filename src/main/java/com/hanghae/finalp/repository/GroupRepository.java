package com.hanghae.finalp.repository;

import com.hanghae.finalp.entity.Group;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {

    //제목과 지역으로 검색
    @Query("select g from Group g where g.groupTitle like CONCAT('%',:title,'%')")
    Slice<Group> findAllByGroupTitleContaining(@Param("title") String title, Pageable pageable);

    @Query("select g from Group g where g.roughAddress in (:addressList)")
    Slice<Group> findAllByRoughAddressIn(@Param("addressList") List<String> addressList, Pageable pageable);

    @Query("select g from Group g where g.roughAddress like CONCAT('%',:title,'%') and g.roughAddress in (:addressList)")
    Slice<Group> findAllByGroupTitleContainingAndRoughAddressIn(@Param("title") String title, @Param("addressList") List<String> addressLIst, Pageable pageable);

    @Query("select distinct g from Group g join fetch g.memberGroups where g.id = :groupId")
    Slice<Group> findMemberByGroupId(@Param("groupId") Long groupId);

}

