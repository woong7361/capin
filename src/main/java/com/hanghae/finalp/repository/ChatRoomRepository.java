package com.hanghae.finalp.repository;

import com.hanghae.finalp.entity.Chatroom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<Chatroom, Long> {
}
