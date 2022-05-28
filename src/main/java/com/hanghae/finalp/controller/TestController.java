package com.hanghae.finalp.controller;

import com.hanghae.finalp.entity.Member;
import com.hanghae.finalp.entity.dto.MemberDto;
import com.hanghae.finalp.repository.MemberRepository;
import com.hanghae.finalp.service.oauth.KakaoOauth;
import com.hanghae.finalp.util.JwtTokenUtils;
import com.hanghae.finalp.util.RedisUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class TestController {

    private final MemberRepository memberRepository;
    private final JwtTokenUtils jwtTokenUtils;
    private final RedisUtils redisUtils;

    @Transactional
    @ResponseBody
    @PostMapping("/dummy-user")
    public MemberDto.refreshTokenRes test(@RequestBody MemberCreateReq memberCreateReq) {
        Member member = Member.createMember("kakaoId", memberCreateReq.getUsername(),
                "https://mj-file-bucket.s3.ap-northeast-2.amazonaws.com/memberDefaultImg.png");
        memberRepository.save(member);
        String accessToken = jwtTokenUtils.createAccessToken(member.getId(), member.getUsername());
        String refreshToken = jwtTokenUtils.createRefreshToken(member.getId());
        redisUtils.setRefreshTokenDataExpire(member.getId().toString(), refreshToken,
                jwtTokenUtils.getRefreshTokenExpireTime(refreshToken));
        return new MemberDto.refreshTokenRes(accessToken, refreshToken);
    }

    /**
     * form test용
     */
    @GetMapping("/form")
    public String form() {
        return "form.html";
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class MemberCreateReq {
        private String username;
    }

    @Data
    public static class Dto {
        public List<Username> username;

        @Data
        public static class Username {
            String a;
        }
    }

}

