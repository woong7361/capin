package com.hanghae.finalp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

public class LoginDto {

    @Data
    @AllArgsConstructor
    public static class Response {
        private String accessToken;
        private String refreshToken;
    }
}
