package com.hanghae.finalp.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class MemberGroupDto {

    @Data
    @AllArgsConstructor
    public static class Request {
        private String startLocationX;
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
