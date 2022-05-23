package com.hanghae.finalp.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class CafeDto {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
//    @RequiredArgsConstructor
    public static class Reqeust {
        //여기 무슨값 받는지 정해지면 밸리데이션 추가할것
        private String locationName;

        //소수점만 입력 가능
        @NotBlank
        @Pattern(regexp="^[0-9]\\d*\\.?\\d*[0-9]", message="숫자만 입력가능합니다.")
        private String locationX;

        @NotBlank
        @Pattern(regexp="^[0-9]\\d*\\.?\\d*[0-9]", message="숫자만 입력가능합니다.")
        private String locationY;

        private String address;
    }


}
