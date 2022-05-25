package com.hanghae.finalp.controller;


import com.hanghae.finalp.config.security.PrincipalDetails;
import com.hanghae.finalp.entity.dto.MemberDto;
import com.hanghae.finalp.entity.dto.other.ResultMsg;
import com.hanghae.finalp.service.LoginService;
import com.hanghae.finalp.service.oauth.KakaoOauth;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final KakaoOauth kakaoOauth;
    private final LoginService longinService;

    /**
     * 카카오 로그인 API
     */
    @GetMapping("/login/oauth2/{provider}")
    public MemberDto.LoginRes loginOAuth(@PathVariable("provider") String provider, @RequestParam String code) {
        return kakaoOauth.login(provider, code);
    }

    /**
     * 토큰 재발급 API
     */
    @PostMapping("/login/refresh-token")
    public MemberDto.refreshTokenRes loginOAuth(@RequestHeader("Authorization") String refreshToken) {
        return longinService.createAccessTokenByRefreshToken(refreshToken);
    }

    /**
     * 로그아웃 API
     */
    @GetMapping("/api/logout")
    public ResultMsg logout(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        longinService.logout(principalDetails.getMemberId());
        return new ResultMsg("success");
    }
}
