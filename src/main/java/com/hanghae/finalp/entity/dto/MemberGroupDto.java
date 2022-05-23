package com.hanghae.finalp.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class MemberGroupDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {
        @NotBlank(message = "위치를 입력해주세요.")
        @Pattern(regexp="^[0-9]\\d*\\.?\\d*[0-9]", message="숫자만 입력가능합니다.")
        private String startLocationX;

        @NotBlank(message = "위치를 입력해주세요.")
        @Pattern(regexp="^[0-9]\\d*\\.?\\d*[0-9]", message="숫자만 입력가능합니다.")
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
