package com.hanghae.finalp.repository;

import com.hanghae.finalp.entity.DmMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DmMessageRepository extends JpaRepository<DmMessage, Long> {
}
