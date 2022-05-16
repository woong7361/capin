package com.hanghae.finalp.controller;


import com.hanghae.finalp.config.security.PrincipalDetails;
import com.hanghae.finalp.dto.LoginDto;
import com.hanghae.finalp.entity.dto.ResultMsg;
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
    public LoginDto.Response loginOAuth(@PathVariable("provider") String provider, @RequestParam String code) {
        return kakaoOauth.login(provider, code);
    }

    /**
     * 토큰 재발급 API
     */
    @PostMapping("/login/refresh-token")
    public LoginDto.refreshTokenRes loginOAuth(@RequestHeader("Authorization") String refreshToken) {
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


    /**
     * principal test용
     */
    @GetMapping("/api/test")
    @ResponseBody
    public String test(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        return principalDetails.toString();
        //com.hanghae.finalp.config.security.PrincipalDetails@252e7b12 나옴
    }
}
