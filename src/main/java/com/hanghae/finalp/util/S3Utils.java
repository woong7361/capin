package com.hanghae.finalp.util;

public final class S3Utils {
    public static final String CLOUD_FRONT_DOMAIN_NAME = "dck3y7rbupkt9.cloudfront.net";

    //이미지 조회시 imageFullUrl가 필요하다 //url에 member.getImageUrl() 넣어주기
    public static String getImgFullUrl(String imgUrl) {
        return "https://" + CLOUD_FRONT_DOMAIN_NAME + "/" + imgUrl;
    }
}
