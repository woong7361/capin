package com.hanghae.finalp.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.hanghae.finalp.config.awsconfig.AWSConfig;
import com.hanghae.finalp.dto.MemberRequestDto;
import com.hanghae.finalp.entity.Member;
import com.hanghae.finalp.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final AWSConfig awsConfig;
    private final AmazonS3 amazonS3;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;


    //내 프로필 조회
/*    public MemberResponseDto getProfile() { //유저정보 받아와야됨

        String imageFullUrl = getFullUrl(member.getImageUrl());

        //member 엔티티를 dto로 바꿔줌
        MemberResponseDto memberResponseDto = new MemberResponseDto(username, imageFullUrl);

        return memberResponseDto;
    }*/


    //내 프로필 생성
/*    public String createMember(MemberRequestDto memberRequestDto, MultipartFile file) throws IOException { //default 이미지는 삭제하는 코드 추가해야됨

        SimpleDateFormat date = new SimpleDateFormat("yyyymmddHHmmss");
        String fileName = file.getOriginalFilename() + "-" + date.format(new Date()); //images.png-20223408153403

        amazonS3.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), null)
                .withCannedAcl(CannedAccessControlList.PublicRead));

        memberRequestDto.setImageUrl(fileName);
*//*
        memberRequestDto.setUsername(username);

        dto가 엔티티로 저장됨
        Member member = new Member(MemberRequestDto memberRequestDto); //username,imageUrl 등등 넣어준다.
        memberRepository.save(member); //db에 파일 저장(생성)
*//*
        return fileName; //images.png-20223408153403
    }*/
    public String uploadFile(MultipartFile file) throws IOException {

        SimpleDateFormat date = new SimpleDateFormat("yyyymmddHHmmss");
        String fileName = file.getOriginalFilename() + "-" + date.format(new Date()); //images.png-20223408153403

        amazonS3.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), null)
                .withCannedAcl(CannedAccessControlList.PublicRead));

        return fileName; //images.png-20223408153403
    }


    //내 프로필 수정
    @Transactional
    public String editMember(MemberRequestDto memberRequestDto, MultipartFile file, Long memberId) throws IOException {

        SimpleDateFormat date = new SimpleDateFormat("yyyymmddHHmmss");
        String fileName = file.getOriginalFilename() + "-" + date.format(new Date()); //images.png-20223408153403

        String currentFilePath = memberRequestDto.getImageUrl();
        // key가 존재하면 기존 파일은 삭제
        if ("".equals(currentFilePath) == false && currentFilePath != null) {
            boolean isExistObject = amazonS3.doesObjectExist(bucket, currentFilePath);

            if (isExistObject == true) {
                amazonS3.deleteObject(bucket, currentFilePath);
            }
        }
        memberRequestDto.setImageUrl(fileName);

        amazonS3.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), null)
                .withCannedAcl(CannedAccessControlList.PublicRead));

        memberRequestDto.setImageUrl(fileName);
        //memberRequestDto.setUsername(username);

        //dto가 엔티티로 저장됨
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("1"));
        member.setImageUrl(fileName);

        return fileName; //images.png-20223408153403
    }
/*    public String editFile(MultipartFile file) throws IOException {

        SimpleDateFormat date = new SimpleDateFormat("yyyymmddHHmmss");
        String fileName = file.getOriginalFilename() + "-" + date.format(new Date()); //images.png-20223408153403


        String currentFilePath = memberRequestDto.getImageUrl();
//         key가 존재하면 기존 파일은 삭제
        if ("".equals(currentFilePath) == false && currentFilePath != null) {
            boolean isExistObject = amazonS3.doesObjectExist(bucket, currentFilePath);

            if (isExistObject == true) {
                amazonS3.deleteObject(bucket, currentFilePath);
            }
        }

        amazonS3.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), null)
                .withCannedAcl(CannedAccessControlList.PublicRead));

        return fileName; //images.png-20223408153403
    }*/



    //계정 삭제할때 넣어주기(미완성)
    public void deleteMember(MemberRequestDto memberRequestDto) throws IOException {
        String currentFilePath = memberRequestDto.getImageUrl();

        // key가 존재할경우 기존 파일은 삭제
        if ("".equals(currentFilePath) == false && currentFilePath != null) {
            boolean isExistObject = amazonS3.doesObjectExist(bucket, currentFilePath);

            if (isExistObject == true) {
                amazonS3.deleteObject(bucket, currentFilePath);
            }
        }
    }

}
