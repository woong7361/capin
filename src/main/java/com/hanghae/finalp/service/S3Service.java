package com.hanghae.finalp.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class S3Service {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final AmazonS3 amazonS3;
    public static final String CLOUD_FRONT_DOMAIN_NAME = "d1ai09q40aghzs.cloudfront.net";



    //이미지 조회시 imageFullUrl가 필요하다 //url에 member.getImageUrl() 넣어주기
    private String getImgFullUrl(String imgUrl) {
        String imageFullUrl= "https://" + CLOUD_FRONT_DOMAIN_NAME + "/" + imgUrl;
        return imageFullUrl;
    }


    public String uploadFile(MultipartFile file) throws IOException {

        SimpleDateFormat date = new SimpleDateFormat("yyyymmddHHmmss");
        String fileName = file.getOriginalFilename() + "-" + date.format(new Date()); //images.png-20223408153403

        amazonS3.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), null)
                .withCannedAcl(CannedAccessControlList.PublicRead));

        return fileName; //images.png-20223408153403
    }


    public String editFile(String currentFilePath, MultipartFile file) throws IOException {
        //currentFilePath = memberRequestDto.getImageUrl() 넣어주기

        SimpleDateFormat date = new SimpleDateFormat("yyyymmddHHmmss");
        String fileName = file.getOriginalFilename() + "-" + date.format(new Date());

        // key가 존재하면 기존 파일은 삭제
        if ("".equals(currentFilePath) == false && currentFilePath != null) {
            boolean isExistObject = amazonS3.doesObjectExist(bucket, currentFilePath);

            if (isExistObject == true) {
                amazonS3.deleteObject(bucket, currentFilePath);
            }
        }

        amazonS3.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), null)
                .withCannedAcl(CannedAccessControlList.PublicRead));

        return fileName;
    }


    public void deleteFile(String currentFilePath) throws IOException {
        //currentFilePath = memberRequestDto.getImageUrl() 넣어주기

        if ("".equals(currentFilePath) == false && currentFilePath != null) {
            boolean isExistObject = amazonS3.doesObjectExist(bucket, currentFilePath);

            if (isExistObject == true) {
                amazonS3.deleteObject(bucket, currentFilePath);
            }
        }
    }

}
