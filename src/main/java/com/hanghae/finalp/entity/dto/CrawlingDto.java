package com.hanghae.finalp.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class CrawlingDto {
    public List<Response> responses;
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        public String title;
        public String imgUrl;
        public String star;
    }
}
