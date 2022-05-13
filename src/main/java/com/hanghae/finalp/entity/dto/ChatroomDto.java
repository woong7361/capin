package com.hanghae.finalp.entity.dto;


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
}
