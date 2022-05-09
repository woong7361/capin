package com.hanghae.finalp.config.awsconfig;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//aws를 사용할 수 있는 AmazonS3Client를 빈으로 생성함
@Configuration
public class AWSConfig {

    //    //aws cloudFront의 베포 도메인 이름을 적어준다(배포 생성시 할당된 기본값)
//    //이미지 조회시 S3 URL(s3Client.getUrl())이 아닌 CloudFront URL(CLOUD_FRONT_DOMAIN_NAME)을 사용하게 됩니다.
//    //ex)S3 키 값(fileName 변수)이 sample.jpg라 할 때, 이미지는 "dq582wpwqowa9.cloudfront.net/sample.jpg" 에서 가져오게 됩니다.
//    public static final String CLOUD_FRONT_DOMAIN_NAME = "d1ai09q40aghzs.cloudfront.net";


    @Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    public static final String CLOUD_FRONT_DOMAIN_NAME = "d1ai09q40aghzs.cloudfront.net";


    @Bean
    public AmazonS3 amazonS3() { //amazonS3가 클라이언트 이다.
        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
        //accessKey와 secretKey를 이용하여 자격증명 객체를 얻습니다.

        return AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }
}
