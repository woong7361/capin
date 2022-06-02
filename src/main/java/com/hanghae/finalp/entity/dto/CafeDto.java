package com.hanghae.finalp.entity.dto;

import com.hanghae.finalp.entity.dto.other.KakaoApiDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

public class CafeDto {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateReq {
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

    @Data
    public static class RecoRes {
        private List<CafeInfo> cafes = new ArrayList<>();
        private LocationRes midLocation;

        public void setMidLocation(Double locationX, Double locationY) {
            this.midLocation = new LocationRes(locationX.toString(), locationY.toString());
        }


        @Data
        public static class CafeInfo extends KakaoApiDto.Document {
            private String mainphotourl;
            private int comntcnt;
            private int scoresum;
            private int scorecnt;

            public CafeInfo(String mainphotourl, int comntcnt, int scoresum, int scorecnt, KakaoApiDto.Document doc) {
                super(doc);
                this.mainphotourl = mainphotourl;
                this.comntcnt = comntcnt;
                this.scoresum = scoresum;
                this.scorecnt = scorecnt;
            }
        }
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RecoReq {
        @NotBlank
        @Pattern(regexp="^[0-9]\\d*\\.?\\d*[0-9]", message="숫자만 입력가능합니다.")
        private String locationX;

        @NotBlank
        @Pattern(regexp="^[0-9]\\d*\\.?\\d*[0-9]", message="숫자만 입력가능합니다.")
        private String locationY;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LocationRes {
        private String locationX;
        private String locationY;
    }



}
