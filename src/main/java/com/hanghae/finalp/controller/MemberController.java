package com.hanghae.finalp.controller;

import com.hanghae.finalp.config.security.PrincipalDetails;
import com.hanghae.finalp.dto.MemberRequestDto;

import com.hanghae.finalp.dto.MemberResponseDto;
import com.hanghae.finalp.entity.dto.ResultMsg;
import com.hanghae.finalp.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;


    //내 프로필 조회
    @GetMapping("/api/profile")
    public MemberResponseDto memberGet(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long memberId = principalDetails.getPrincipal().getMemberId();
        MemberResponseDto memberResponseDto = memberService.getMyProfile(memberId);
        return memberResponseDto;
    }



    //내 프로필 생성 ->로그인때 카카오아이디 받아와서, db에 멤버가 없을 경우 만들어주기때문에 프로필은 생성이 필요없다


    @PostMapping("/api/profile/edit")
    public ResultMsg memberEdit(@RequestPart(required = false) MemberRequestDto memberRequestDto,
                             @RequestPart(value = "file", required = false) MultipartFile file,
                             @AuthenticationPrincipal PrincipalDetails principalDetails) throws IOException {

        Long memberId = principalDetails.getPrincipal().getMemberId();
//        if(memberRequestDto!= null) { //username이 있으면
//        if (memberRequestDto.getUsername() != null && !memberRequestDto.getUsername().isEmpty()) { //isEmpty는 안됨
            if (memberRequestDto != null && memberRequestDto.getUsername().length() > 0) {
                String username = memberRequestDto.getUsername();
                memberService.editMyProfile(username, file, memberId);
            }else{ //username 없으면
                memberService.editMyProfile(file, memberId);
            }
        return new ResultMsg("success");
    }


    @GetMapping("/api/withdraw")
    public ResultMsg memberDelete(@AuthenticationPrincipal PrincipalDetails principalDetails) throws IOException {
        Long memberId = principalDetails.getPrincipal().getMemberId();
        memberService.deleteMember(memberId);
        return new ResultMsg("success");
    }

}


