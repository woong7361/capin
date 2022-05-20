package com.hanghae.finalp.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

public class CafeDto {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Reqeust {
        //여기 무슨값 받는지 정해지면 밸리데이션 추가할것
        private String locationName;

        @NotBlank(message = "위치를 입력해주세요.") @Positive
        String locationX;

        @NotBlank(message = "위치를 입력해주세요.") @Positive
        String locationY;

        String address;
    }
}
