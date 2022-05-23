package com.hanghae.finalp.service;

import com.hanghae.finalp.config.exception.customexception.entity.MemberNotExistException;
import com.hanghae.finalp.entity.Member;
import com.hanghae.finalp.entity.dto.MemberDto;
import com.hanghae.finalp.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;



@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final S3Service s3Service;

    //내 프로필 조회
    public MemberDto.ProfileRes getMyProfile(Long memberId) {
       Member member = memberRepository.findById(memberId).orElseThrow(
               MemberNotExistException::new);
        //member 엔티티를 dto로 바꿔줌
        return new MemberDto.ProfileRes(member.getUsername(), member.getImageUrl());
    }

    //프로필 수정
    @Transactional
    public MemberDto.ProfileRes editMyProfile(String username, MultipartFile file, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                MemberNotExistException::new);

        String currentFilePath = member.getImageUrl();
        String fullFilePath = s3Service.uploadFile(file); //이미지 조회시 imageFullUrl가 필요하다

        member.patchMember(username, fullFilePath);
        s3Service.deleteFile(currentFilePath);

        return new MemberDto.ProfileRes(member.getUsername(), member.getImageUrl());
    }

    //회원탈퇴
    @Transactional
    public void deleteMember(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                MemberNotExistException::new);
        s3Service.deleteFile(member.getImageUrl()); //카카오 이미지가 아닐경우에만 이거 해주게됨
        memberRepository.delete(member);
    }
}
