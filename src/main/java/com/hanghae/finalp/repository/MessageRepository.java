package com.hanghae.finalp.repository;

import com.hanghae.finalp.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {

    Slice<Message> findByChatroomId(Long chatroomId, Pageable pageable);
}
