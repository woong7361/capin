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
//@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class S3Service {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.cloudFront.distributionDomain}")
    private String CLOUD_FRONT_DOMAIN_NAME;

//    public static final String CLOUD_FRONT_DOMAIN_NAME = "d1ai09q40aghzs.cloudfront.net";


    //이미지 조회시 imageFullUrl가 필요하다
    public String getFullPath(String fileName) {
        String fullFileName= "https://" + CLOUD_FRONT_DOMAIN_NAME + "/" + fileName;
        return fullFileName;
    }


    public String uploadFile(MultipartFile file) throws IOException {

        SimpleDateFormat date = new SimpleDateFormat("yyyymmddHHmmss");
        String fileName = file.getOriginalFilename() + "-" + date.format(new Date());

        amazonS3.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), null)
                .withCannedAcl(CannedAccessControlList.PublicRead));

        return fileName;
    }


    public String editFile(String currentFilePath, MultipartFile file) throws IOException {
        //currentFilePath = member.getImageUrl();이다

        // key가 존재하면 기존 파일은 삭제
        if ("".equals(currentFilePath) == false && currentFilePath != null) {
            boolean isExistObject = amazonS3.doesObjectExist(bucket, currentFilePath);

            if (isExistObject == true) {
                amazonS3.deleteObject(bucket, currentFilePath);
            }
        }

        SimpleDateFormat date = new SimpleDateFormat("yyyymmddHHmmss");
        String fileName = file.getOriginalFilename() + "-" + date.format(new Date());

        amazonS3.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), null)
                .withCannedAcl(CannedAccessControlList.PublicRead));

        return fileName;
    }

/*    public String makeUniqueFileName(MultipartFile file){
        SimpleDateFormat date = new SimpleDateFormat("yyyymmddHHmmss");
        return file.getOriginalFilename() + "-" + date.format(new Date()); //return fileName
    }*/


/*    public String makeOriginalFileName(String uniqueImgUrl){
        int line = uniqueImgUrl.lastIndexOf("-");
        return uniqueImgUrl.substring(0,line); //return imageUrl 에서 시간뗀것
    }*/



    public void deleteFile(String currentFilePath) throws IOException {
        //currentFilePath = memberRequestDto.getImageUrl() 넣어주기

        if ("".equals(currentFilePath) == false && currentFilePath != null) {
            int slash = currentFilePath.lastIndexOf("/");
            String filePath = currentFilePath.substring(slash+1);

            boolean isExistObject = amazonS3.doesObjectExist(bucket, filePath);

            if (isExistObject == true) {
                amazonS3.deleteObject(bucket, filePath);
            }
        }
    }

}
