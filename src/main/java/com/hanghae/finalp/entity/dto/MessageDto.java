package com.hanghae.finalp.entity.dto;

import com.hanghae.finalp.entity.Message;
import com.hanghae.finalp.entity.mappedsuperclass.MessageType;
import com.hanghae.finalp.entity.mappedsuperclass.RoomType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MessageDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Reqeust {
        Long chatroomId;
        Long senderId;
        String senderName;
        String content;
        MessageType messageType;
    }
    @Data
    @NoArgsConstructor

    public static class Send {
        String chatroomId;
        Long senderId;
        String senderName;
        String content;
        MessageType messageType;
        List<ChatMember> members = new ArrayList<>();

        public Send(Message message) {
            this.chatroomId = message.getChatroom().getId().toString();
            this.senderId = message.getSenderId();
            this.senderName = message.getSenderName();
            this.content = message.getContent();
            this.messageType = message.getMessageType();
        }

        public Send(String roomId, Long memberId, String username, MessageType messageType) {
            this.chatroomId = roomId;
            this.senderId = memberId;
            this.senderName = username;
            this.messageType = messageType;
        }
    }

    @Data
    @AllArgsConstructor
    public static class ChatMember {
        Long memberId;
        String username;
    }

}
