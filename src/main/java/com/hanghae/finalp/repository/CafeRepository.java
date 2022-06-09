package com.hanghae.finalp.repository;

import com.hanghae.finalp.entity.Cafe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CafeRepository extends JpaRepository<Cafe, Long> {
    void deleteByGroupId(long GroupId);
    void deleteByGroup_Id(long GroupId);

}
