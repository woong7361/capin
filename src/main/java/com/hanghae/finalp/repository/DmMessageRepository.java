package com.hanghae.finalp.repository;


import com.hanghae.finalp.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DmMessageRepository extends JpaRepository<Message, Long> {
}
