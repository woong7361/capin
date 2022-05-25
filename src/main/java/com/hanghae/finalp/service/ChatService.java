package com.hanghae.finalp.service;

import com.hanghae.finalp.config.exception.customexception.entity.EntityNotExistException;
import com.hanghae.finalp.entity.Chatroom;
import com.hanghae.finalp.entity.Message;
import com.hanghae.finalp.entity.dto.MessageDto;
import com.hanghae.finalp.entity.mappedsuperclass.MessageType;
import com.hanghae.finalp.repository.ChatRoomRepository;
import com.hanghae.finalp.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class ChatService {
    // Redis CacheKeys

    private final RedisTemplate redisTemplate;
    private final ChannelTopic channelTopic;

    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;


    public String getRoomId(String destination) {
        int lastIndex = destination.lastIndexOf('/');
        if (lastIndex != -1) {
            return destination.substring(lastIndex + 1);
        } else {
            return "";
        }
    }

    @Transactional
    public void sendChatMessage(MessageDto.SendRes message, Set<String> roomMembers) {
        if (message.getMessageType().equals(MessageType.ENTER)) {
            message.setContent(message.getSenderName() + "님이 입장하였습니다.");
            message.setSenderName("[알림]");
        } else if (message.getMessageType().equals(MessageType.QUIT)) {
            message.setContent(message.getSenderName() + "님이 퇴장하였습니다.");
            message.setSenderName("[알림]");
        }
        List<MessageDto.ChatMember> members = roomMembers.stream().
                map((mem) -> new MessageDto.ChatMember(Long.valueOf(mem.split("_")[0]), mem.split("_")[1]))
                .collect(Collectors.toList());
        message.setMembers(members);

        saveMessage(Long.valueOf(message.getChatroomId()), message.getSenderId(), message.getSenderName(),
                message.getContent(), message.getMessageType());


        redisTemplate.convertAndSend(channelTopic.getTopic(), message);
    }

    @Transactional
    public void saveMessage(Long chatroomId, Long senderId, String senderName, String content, MessageType messageType) {
        Chatroom chatroom = chatRoomRepository.findById(chatroomId)
                .orElseThrow(EntityNotExistException::new);

        Message saveMessage =
                Message.createMessage(senderId, senderName, content, messageType, chatroom);
        messageRepository.save(saveMessage);
    }
}

