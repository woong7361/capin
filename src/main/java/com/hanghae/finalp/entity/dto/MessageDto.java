package com.hanghae.finalp.entity.dto;

import com.hanghae.finalp.entity.mappedsuperclass.MessageType;
import com.hanghae.finalp.entity.mappedsuperclass.RoomType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class MessageDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Reqeust {
        Long chatroomId;
        Long senderId;             //erase
        String content;
        MessageType messageType;   //erase
        RoomType roomType;
        String senderName;
    }

    @Data
    @Builder
    public static class Send {
        String chatroomId;
        Long senderId;
        String senderName;
        String content;
        MessageType messageType;
        RoomType roomType;
    }


}
