package com.hanghae.finalp.controller;

import com.hanghae.finalp.config.security.PrincipalDetails;
import com.hanghae.finalp.dto.MemberRequestDto;
import com.hanghae.finalp.entity.Member;
import com.hanghae.finalp.repository.MemberRepository;
import com.hanghae.finalp.service.MemberService;
import com.hanghae.finalp.service.S3Service;
import com.hanghae.finalp.util.S3Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    private final S3Service s3Service;
    private final MemberRepository memberRepository;


    //내 프로필 정보 조회
//    @GetMapping("/api/profile")
//    public String memberGet(Model model) { //유저 정보 받아와서
//        MemberResponseDto memberResponseDto = memberService.getProfile();
//        model.addAttribute("memberResponseDto", memberResponseDto);
//        return "/"; //프로필 정보 조회하는 페이지로
//    }

/*    //내 프로필 생성
    @PostMapping("/api/profile/create") //api는 임의. 해당하는 사용자의 정보 추가해야됨
    public String memberCreate(MemberRequestDto memberRequestDto, MultipartFile file) throws IOException {
        String fileName = memberService.createMember(memberRequestDto, file);
        //fileName => images.png-20223408153403 가 된다.
        return "redirect:/gallery";
    }  */

//    @PostMapping("/api/profile/create") //api는 임의. 해당하는 사용자의 정보 추가해야됨
//    public String memberCreate(MultipartFile file) throws IOException {
//        String fileName = memberService.createMember(file);
//        //fileName => images.png-20223408153403 가 된다.
//        return "redirect:/gallery";
//    }


    //내 프로필 수정
    @PostMapping("/api/profile/edit") //해당하는 사용자의 정보 추가해야됨-> 그래서 유저네임 바꾸는 것도 추가하기
    @ResponseBody
    public String memberEdit(MemberRequestDto memberRequestDto, MultipartFile file,
    @AuthenticationPrincipal PrincipalDetails principalDetails
    ) throws IOException {
        String fileName = memberService.editMember(memberRequestDto, file, principalDetails.getPrincipal().getMemberId());
        //fileName => images.png-20223408153403 가 된다.

        return "success";
    }

    //계정 삭제시 s3과 db에 이미지 삭제도 넣어주기
    //controller에서는 아래 2줄 추가
    // MemberService.delete(memberRequestDto); //s3에 있는거 삭제
    //MemberRepository.deleteById(memberRequestDto.getId());  //db에 있는것도 삭제하기

}


