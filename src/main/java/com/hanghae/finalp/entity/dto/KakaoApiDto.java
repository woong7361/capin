package com.hanghae.finalp.entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class KakaoApiDto {
    public List<Document> documents;
    @Data
    public static class Document {
    public String id; //장소 id
    public String category_group_code; //카테고리 그룹 코드
    public String category_group_name; //카테고리 글룹명
    public String category_name; //카테고리 이름
    public String distance; //중심좌표까지의 거리
    public String phone; //전화번호
    public String place_name; //장소명,업체명
    public String place_url; //장소 상세페이지 url
    public String road_address_name; //전체 도로명 주소
    public String x;
    public String y;
    }
}
