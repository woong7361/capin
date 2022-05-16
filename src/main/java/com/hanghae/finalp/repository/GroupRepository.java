package com.hanghae.finalp.repository;

import com.hanghae.finalp.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {

//      //페이징
//      Page<Group> findALL(Pageable pageable);
//
//      //제목과 지역으로 검색
//      Page<Group> findByGroupTitleContainingOrRoughAddressContaining(String searchKeyword, Pageable pageable);
//
//      Optional<Group> findById(Long groupId);
//

}
