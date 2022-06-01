package com.hanghae.finalp.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import com.hanghae.finalp.config.exception.customexception.etc.S3Exception;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
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


    /**
     * S3에 파일 업로드
     */
    public String uploadFile(MultipartFile file) {
        if(file == null) return null;
        log.debug("custom log:: upload file to S3...");
        SimpleDateFormat date = new SimpleDateFormat("yyyymmddHHmmss");
        String fileName = file.getOriginalFilename() + "-" + date.format(new Date());

        try {
            byte[] bytes = IOUtils.toByteArray(file.getInputStream());
            ObjectMetadata metaData = new ObjectMetadata();
            metaData.setContentLength(bytes.length);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

            amazonS3.putObject(new PutObjectRequest(bucket, fileName, byteArrayInputStream, metaData)
//            amazonS3.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), null)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new S3Exception();
        }
        return "https://" + CLOUD_FRONT_DOMAIN_NAME + "/" + fileName;
    }


    /**
     * S3에서 파일 삭제
     */
    public void deleteFile(String currentFilePath) {
        if (currentFilePath == null) return;
        if (currentFilePath.equals("https://mj-file-bucket.s3.ap-northeast-2.amazonaws.com/memberDefaultImg.png")) return;
        if (currentFilePath.equals("https://mj-file-bucket.s3.ap-northeast-2.amazonaws.com/groupDefaultImg.png")) return;
        if (currentFilePath.startsWith("http://k.kakaocdn.net")) return;

        String filePath = currentFilePath.substring(currentFilePath.lastIndexOf("/") + 1);

        if (amazonS3.doesObjectExist(bucket, filePath)) {
            log.info("custom log:: delete file from S3...");
            amazonS3.deleteObject(bucket, filePath);
        }
    }
}