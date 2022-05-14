package com.hanghae.finalp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

public class LoginDto {

    @Data
    @AllArgsConstructor
    public static class Response {
        private String accessToken;
        private String refreshToken;
        private MemberRes member;
        private Boolean isFirst;

        public Response(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }

        public Response(MemberRes member, Boolean isFirst) {
            this.member = member;
            this.isFirst = isFirst;
        }
    }

    @Data
    @AllArgsConstructor
    public static class refreshTokenRes {
        private String accessToken;
        private String refreshToken;

    }

    @Data
    @AllArgsConstructor
    public static class MemberRes {
        private Long memberId;
        private String username;
        private String imageUrl;
    }

}
