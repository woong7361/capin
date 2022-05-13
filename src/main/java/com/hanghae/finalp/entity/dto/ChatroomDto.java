package com.hanghae.finalp.entity.dto;


import com.hanghae.finalp.entity.ChatMember;
import com.hanghae.finalp.entity.mappedsuperclass.RoomType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ChatroomDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateReq {
        private Long sideMemberId;
    }

    @Data
    @AllArgsConstructor
    public static class CreateRes {
        private Long roomId;
    }

    @Data
    @AllArgsConstructor
    public static class RoomRes {
        private Long roomId;
        private String title;
        private RoomType roomType;

        public RoomRes(ChatMember chatMember) {
            this.roomId = chatMember.getChatroom().getId();
            this.title = chatMember.getChatroom().getChatroomTitle();
            this.roomType = chatMember.getChatroom().getRoomType();
        }
    }


}
