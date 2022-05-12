package com.hanghae.finalp.service;

import com.amazonaws.services.s3.AmazonS3;
import com.hanghae.finalp.dto.MemberResponseDto;
import com.hanghae.finalp.entity.Member;
import com.hanghae.finalp.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;

    private final AmazonS3 amazonS3;
    private final S3Service s3Service;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;




    //내 프로필 조회
    public MemberResponseDto getMyProfile(Long memberId) { //유저정보 받아와야됨
        Member member= memberRepository.findById(memberId).orElseThrow(
                () -> new IllegalArgumentException("해당 memberId가 존재하지 않습니다.")
        );
        String imageUrl = member.getImageUrl();
        String username = member.getUsername();

        //member 엔티티를 dto로 바꿔줌
        MemberResponseDto memberResponseDto = new MemberResponseDto(username, imageUrl);
        return memberResponseDto;
    }


    //프로필 수정
    @Transactional
    public void editMyProfile(String username, MultipartFile file, Long memberId) throws IOException {
        Member member= memberRepository.findById(memberId).orElseThrow(
                () -> new IllegalArgumentException("해당 memberId가 존재하지 않습니다.")
        );

        String prevUsername = member.getUsername();
        if(username != null){
            if(!prevUsername.equals(username)){
                member.patchUsername(username);
            }
        }
        if (file != null && file.getSize() > 0) {
            //s3 변경
            String currentFilePath = member.getImageUrl();
            String fileName = s3Service.editFile(currentFilePath, file);
            //db 변경
            String fullFileName = s3Service.getFullPath(fileName);
            member.patchImageUrl(fullFileName);
        }else{ //수정할 file이 없는 경우
            //s3 변경
            String currentFilePath = member.getImageUrl();
            s3Service.deleteFile(currentFilePath);
            //db 변경
            member.patchImageUrl(null);
        }
    }

    //프로필 수정
    @Transactional
    public void editMyProfile(MultipartFile file, Long memberId) throws IOException {
        Member member= memberRepository.findById(memberId).orElseThrow(
                () -> new IllegalArgumentException("해당 memberId가 존재하지 않습니다.")
        );

        if (file != null && file.getSize() > 0) {
            //s3 변경
            String currentFilePath = member.getImageUrl();
            String fileName = s3Service.editFile(currentFilePath, file);
            //db 변경
            String fullFileName = s3Service.getFullPath(fileName);
            member.patchImageUrl(fullFileName);
        }else{ //수정할 file이 없는 경우
            //s3 변경
            String currentFilePath = member.getImageUrl();
            s3Service.deleteFile(currentFilePath);
            //db 변경
            member.patchImageUrl(null);
        }
    }


    //회원탈퇴
    @Transactional
    public void deleteMember(Long memberId) throws IOException {
        Member member= memberRepository.findById(memberId).orElseThrow(
                () -> new IllegalArgumentException("해당 memberId가 존재하지 않습니다.")
        );

        String currentFilePath = member.getImageUrl();
        s3Service.deleteFile(currentFilePath); //카카오 이미지가 아닐경우에만 이거 해주게됨
        memberRepository.delete(member);
    }

}
