package com.hanghae.finalp.controller;

import com.hanghae.finalp.config.security.PrincipalDetails;
import com.hanghae.finalp.entity.dto.MessageDto;
import com.hanghae.finalp.entity.mappedsuperclass.MessageType;
import com.hanghae.finalp.service.ChatService;
import com.hanghae.finalp.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MessageController {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic channelTopic;
    private final ChatService chatService;
    private final MessageService messageService;

    /**
     * webSocket  --  /pub/chat 으로 들어온 메시징 처리
     */
    @MessageMapping("/channel")
    public void redisMessage(MessageDto.Reqeust message) {
        message.setMessageType(MessageType.TALK);

        chatService.saveMessage(message.getChatroomId(), message.getSenderId(),message.getSenderName(), message.getContent(), message.getMessageType());

        redisTemplate.convertAndSend(channelTopic.getTopic(), message);
    }


    @GetMapping("/api/messages/{chatroomId}")
    public Slice<MessageDto.Send> getPreviousMessage(@PathVariable("chatroomId") Long chatroomId, Pageable pageable) {
        return messageService.getPreviousMessage(chatroomId, pageable);
    }
}
