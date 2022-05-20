package com.hanghae.finalp.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

public class MemberGroupDto {

    @Data
    @AllArgsConstructor
    public static class Request {
        @NotBlank(message = "위치를 입력해주세요.") @Positive
        private String startLocationX;

        @NotBlank(message = "위치를 입력해주세요.") @Positive
        private String startLocationY;

        private String startAddress;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private String startLocationX;
        private String startLocationY;
        private String startAddress;
    }
}
