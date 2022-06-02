package com.hanghae.finalp.entity.dto;

import com.hanghae.finalp.entity.Member;
import com.hanghae.finalp.entity.MemberGroup;
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
        private Long memberId;
        private String username;
        private String imageUrl;
        private Authority authority;

        public SpecificRes(OwnerSpecificRes ownerSpecificRes) {
            this.memberId = ownerSpecificRes.getMemberId();
            this.username = ownerSpecificRes.getUsername();
            this.imageUrl = ownerSpecificRes.getImageUrl();
            this.authority = ownerSpecificRes.getAuthority();
        }
        public SpecificRes(JoinSpecificRes joinSpecificRes) {
            this.memberId = joinSpecificRes.getMemberId();
            this.username = joinSpecificRes.getUsername();
            this.imageUrl = joinSpecificRes.getImageUrl();
            this.authority = joinSpecificRes.getAuthority();
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OwnerSpecificRes {
        private Long memberId;
        private String username;
        private String imageUrl;
        private Authority authority;

        public OwnerSpecificRes(Member member) {
            this.memberId = member.getId();
            this.username = member.getUsername();
            this.imageUrl = member.getImageUrl();
            this.authority = Authority.OWNER;
        }
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class JoinSpecificRes {
        private Long memberId;
        private String username;
        private String imageUrl;
        private Authority authority;

        public JoinSpecificRes(Member member) {
            this.memberId = member.getId();
            this.username = member.getUsername();
            this.imageUrl = member.getImageUrl();
            this.authority = Authority.JOIN;
        }

        public JoinSpecificRes(MemberGroup memberGroup) {
            this.memberId = memberGroup.getMember().getId();
            this.username = memberGroup.getMember().getUsername();
            this.imageUrl = memberGroup.getMember().getImageUrl();
            this.authority = memberGroup.getAuthority();
        }
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
