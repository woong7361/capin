package com.hanghae.finalp.entity.dto;

import com.hanghae.finalp.entity.mappedsuperclass.Authority;
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
        private Long memberId;
        private String username;
        private String imageUrl;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SpecificRes {
        private Long userId;
        private String username;
        private String imageUrl;
        private Authority authority;
    }

    @Data
    @AllArgsConstructor
    public static class LoginRes {
        private String accessToken;
        private String refreshToken;
        private MemberRes member;
        private Boolean isFirst;

        public LoginRes(MemberRes member, Boolean isFirst) {
            this.member = member;
            this.isFirst = isFirst;
        }
    }

    @Data
    @AllArgsConstructor
    public static class MemberRes {
        private Long memberId;
        private String username;
        private String imageUrl;
    }

    @Data
    @AllArgsConstructor
    public static class refreshTokenRes {
        private String accessToken;
        private String refreshToken;
    }


}
