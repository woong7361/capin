package com.hanghae.finalp.controller;

import com.hanghae.finalp.dto.LoginDto;
import com.hanghae.finalp.entity.Member;
import com.hanghae.finalp.repository.MemberRepository;
import com.hanghae.finalp.service.oauth.KakaoOauth;
import com.hanghae.finalp.util.JwtTokenUtils;
import com.hanghae.finalp.util.RedisUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;

@Controller
@RequiredArgsConstructor
public class TestController {

    private final MemberRepository memberRepository;
    private final KakaoOauth kakaoOauth;
    private final JwtTokenUtils jwtTokenUtils;
    private final RedisUtils redisUtils;

    @Transactional
    @ResponseBody
    @PostMapping("/dummy-user")
    public LoginDto.refreshTokenRes test(@RequestBody MemberCreateReq memberCreateReq) {
        Member member = Member.createMember("kakaoId", memberCreateReq.getUsername(), null);
        memberRepository.save(member);
        String accessToken = jwtTokenUtils.createAccessToken(member.getId(), member.getUsername());
        String refreshToken = jwtTokenUtils.createRefreshToken(member.getId());
        redisUtils.setDataExpire(member.getId().toString(), refreshToken, 14 * JwtTokenUtils.DAY);
        return new LoginDto.refreshTokenRes(accessToken, refreshToken);
    }

    /**
     * form test용
     */
    @GetMapping("/form")
    public String form() {

        return "form.html";
    }


    @PostConstruct
    public void createDummyMember() {
        Member dummy = Member.createMember("1", "testUser", null);
        memberRepository.save(dummy);
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class MemberCreateReq {
        private String username;
    }

}

