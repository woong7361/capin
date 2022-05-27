package com.hanghae.finalp.entity.dto.other;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class KakaoApiDto {
    private List<Document> documents;
    @Data
    @NoArgsConstructor
    public static class Document {
        private String id; //장소 id
        private String category_group_code; //카테고리 그룹 코드
        private String category_group_name; //카테고리 글룹명
        private String category_name; //카테고리 이름
        private String distance; //중심좌표까지의 거리
        private String phone; //전화번호
        private String place_name; //장소명,업체명
        private String place_url; //장소 상세페이지 url
        private String road_address_name; //전체 도로명 주소
        private String x;
        private String y;

        public Document(Document document) {
            this.id = document.getId();
            this.category_group_code = document.getCategory_group_code();
            this.category_group_name = document.getCategory_group_name();
            this.category_name = document.getPlace_name();
            this.distance = document.getDistance();
            this.phone = document.getPhone();
            this.place_name = document.getPlace_name();
            this.place_url = document.getPlace_url();
            this.road_address_name = document.getRoad_address_name();
            this.x = document.getX();
            this.y = document.getY();
        }
    }
}
