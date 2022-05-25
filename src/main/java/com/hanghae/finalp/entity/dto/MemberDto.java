package com.hanghae.finalp.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

public class MemberDto {
    @Data
    @AllArgsConstructor
    public static class Principal {
        private Long memberId;
        private String username;
    }

    @Data
    @AllArgsConstructor
    public static class RedisPrincipal implements Serializable {
        private Long memberId;
        private String username;
        private Long roomId;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProfileRes {
        private String username;
        private String imageUrl;
    }

}
