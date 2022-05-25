

package com.hanghae.finalp.config.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae.finalp.entity.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;


/**
 * redis를 통한 SUB
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class RedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final RedisTemplate redisTemplate;
    private final SimpMessageSendingOperations messagingTemplate;

    /**
     * Redis에서 메시지가 발행(publish)되면 대기하고 있던 onMessage가 해당 메시지를 받아 처리한다.
     */
    public void sendMessage(String publishMessage) {

        System.out.println("publishMessage = " + publishMessage);

        try {
            MessageDto.SendRes message = objectMapper.readValue(publishMessage, MessageDto.SendRes.class);
            messagingTemplate.convertAndSend("/sub/channel/" + message.getChatroomId().toString(), message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }



    //지워도 될까?
    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            // redis에서 발행된 데이터를 받아 deserialize
            String publishMessage = (String) redisTemplate.getStringSerializer().deserialize(message.getBody());
            // ChatMessage 객채로 맵핑
            MessageDto.SendRes message1 = objectMapper.readValue(publishMessage, MessageDto.SendRes.class);
            // Websocket 구독자에게 채팅 메시지 Send
            messagingTemplate.convertAndSend("/sub/channel/" + message1.getChatroomId(), message1);
        } catch (Exception e) {
            System.out.println("e = " + e);
            log.error(e.getMessage());
        }
    }
}


