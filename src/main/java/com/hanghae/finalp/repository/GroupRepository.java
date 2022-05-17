package com.hanghae.finalp.repository;

import com.hanghae.finalp.entity.Group;
import com.hanghae.finalp.entity.dto.GroupDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {


      Page<GroupDto.SimpleRes> findByGroupTitleContainingOrRoughAddressContaining(String searchKeyword, Pageable pageable);


}
