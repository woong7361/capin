package com.hanghae.finalp.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.hanghae.finalp.config.exception.customexception.S3Exception;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class S3Service {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final AmazonS3 amazonS3;
    @Value("${cloud.aws.cloudFront.distributionDomain}")
    private String CLOUD_FRONT_DOMAIN_NAME;


    public String uploadFile(MultipartFile file) {
        if(file == null) return null;

        SimpleDateFormat date = new SimpleDateFormat("yyyymmddHHmmss");
        String fileName = file.getOriginalFilename() + "-" + date.format(new Date());

        try {
            amazonS3.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), null)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new S3Exception();
        }
        String fullFilePath = "https://" + CLOUD_FRONT_DOMAIN_NAME + "/" + fileName;
        return fullFilePath;
    }



    public void deleteFile(String currentFilePath) {
        if (currentFilePath == null) return;
        if (currentFilePath.startsWith("http://k.kakaocdn.net")) return;

        String filePath = currentFilePath.substring(currentFilePath.lastIndexOf("/") + 1);

        if (amazonS3.doesObjectExist(bucket, filePath)) {
            log.info("S3파일 삭제");
            amazonS3.deleteObject(bucket, filePath);
        }
    }
}