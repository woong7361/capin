package com.hanghae.finalp.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class CrawlingDto {
    private List<Response> responses;
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private String title;
        private String imgUrl;
        private String star;
    }
}
