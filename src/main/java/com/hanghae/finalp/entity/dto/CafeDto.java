package com.hanghae.finalp.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class CafeDto {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Reqeust {
        private String locationName;
        String locationX;
        String locationY;
        String address;
    }
}
