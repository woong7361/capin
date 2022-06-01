package com.hanghae.finalp.service;

import com.hanghae.finalp.config.exception.customexception.entity.MemberNotExistException;
import com.hanghae.finalp.entity.Member;
import com.hanghae.finalp.entity.dto.MemberDto;
import com.hanghae.finalp.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final S3Service s3Service;

    /**
     * 내 프로필 조회
     */
    public MemberDto.ProfileRes getMyProfile(Long memberId) {
       Member member = memberRepository.findById(memberId).orElseThrow(MemberNotExistException::new);
        return new MemberDto.ProfileRes(member.getId(), member.getUsername(), member.getImageUrl());
    }

    /**
     * 프로필 수정
     */
    @Transactional()
    public MemberDto.ProfileRes editMyProfile(String username, MultipartFile multipartFile, Long memberId){
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotExistException::new);

        String imageUrl = member.getImageUrl();
        if(multipartFile != null) {
            s3Service.deleteFile(imageUrl);
            imageUrl = s3Service.uploadFile(multipartFile);
        }
        member.patchMember(username, imageUrl);

        return new MemberDto.ProfileRes(member.getId(), member.getUsername(), member.getImageUrl());
    }

    /**
     * 회원탈퇴
     */
    @Transactional
    public void deleteMember(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotExistException::new);
        s3Service.deleteFile(member.getImageUrl());
        memberRepository.deleteById(memberId);
    }
}
