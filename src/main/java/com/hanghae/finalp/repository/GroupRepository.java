package com.hanghae.finalp.repository;

import com.hanghae.finalp.entity.Group;
import com.hanghae.finalp.entity.dto.GroupDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {

      //페이징
      Page<GroupDto.SimpleRes> findALL(Pageable pageable);

      //제목과 지역으로 검색
      Page<GroupDto.SimpleRes> findByGroupTitleContainingOrRoughAddressContaining(String searchKeyword, Pageable pageable);

      Optional<Group> findById(Long groupId);

}
