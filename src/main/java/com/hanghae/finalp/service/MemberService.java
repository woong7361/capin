package com.hanghae.finalp.service;

import com.hanghae.finalp.config.exception.customexception.EntityNotExistException;
import com.hanghae.finalp.entity.Member;
import com.hanghae.finalp.entity.dto.MemberDto;
import com.hanghae.finalp.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static com.hanghae.finalp.config.exception.code.ErrorMessageCode.ENTITY_NOT_FOUND_CODE;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final S3Service s3Service;

    //내 프로필 조회
    public MemberDto.ProfileRes getMyProfile(Long memberId) {
        Member member = findMemberFromDB(memberId);
        //member 엔티티를 dto로 바꿔줌
        return new MemberDto.ProfileRes(member.getUsername(), member.getImageUrl());
    }

    //프로필 수정
    @Transactional
    public MemberDto.ProfileRes editMyProfile(String username, MultipartFile file, Long memberId) {
        Member member = findMemberFromDB(memberId);
        String currentFilePath = member.getImageUrl();
        String newFilePath = s3Service.uploadFile(file);
        member.patchMember(username, newFilePath);
        s3Service.deleteFile(currentFilePath);

        return new MemberDto.ProfileRes(member.getUsername(), member.getImageUrl());
    }

    //회원탈퇴
    @Transactional
    public void deleteMember(Long memberId) {
        Member member = findMemberFromDB(memberId);

        s3Service.deleteFile(member.getImageUrl()); //카카오 이미지가 아닐경우에만 이거 해주게됨
        memberRepository.delete(member);
    }


    //===================================================================================================//


    private Member findMemberFromDB(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(
                () -> new EntityNotExistException(ENTITY_NOT_FOUND_CODE, "해당 memberId가 존재하지 않습니다.")
        );
    }

}
