package com.hanghae.finalp.controller;

import com.hanghae.finalp.config.security.PrincipalDetails;
import com.hanghae.finalp.dto.LoginDto;
import com.hanghae.finalp.service.oauth.KakaoOauth;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final KakaoOauth kakaoOauth;

    /**
     * 카카오 로그인 API
     */
    @GetMapping("/login/oauth2/{provider}")
    @ResponseBody
    public ResponseEntity<LoginDto.Response> loginOAuth(@PathVariable("provider") String provider, @RequestParam String code) {
        return kakaoOauth.login(provider, code);
    }

    @PostMapping("/login/refresh-token")
    @ResponseBody
    public ResponseEntity<LoginDto.Response> loginOAuth(@RequestHeader("Authorization") String refreshToken) {
        return kakaoOauth.createAccessTokenByRefreshToken(refreshToken);
    }

    /**
     * form test용
     */
    @GetMapping("/form")
    public String form() {
        return "form.html";
    }

    /**
     * principal test용
     */
    @GetMapping("/api/test")
    @ResponseBody
    public String test(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        return principalDetails.toString();
    }
}
