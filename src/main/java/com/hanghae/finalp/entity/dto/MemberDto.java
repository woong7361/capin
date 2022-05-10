package com.hanghae.finalp.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

public class MemberDto {
    @Data
    @AllArgsConstructor
    public static class Principal {
        private Long memberId;
        private String username;

    }


}
