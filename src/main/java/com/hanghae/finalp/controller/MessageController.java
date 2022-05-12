package com.hanghae.finalp.controller;

import com.hanghae.finalp.entity.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MessageController {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic channelTopic;

    /**
     * webSocket  --  /pub/chat 으로 들어온 메시징 처리
     */
    @MessageMapping("/chat")
    public void redisMessage(MessageDto.Reqeust message) {
        redisTemplate.convertAndSend(channelTopic.getTopic(), message);
    }

}
