package com.hanghae.finalp.controller;

import com.hanghae.finalp.config.exception.dto.ResultMsg;
import com.hanghae.finalp.config.security.PrincipalDetails;
import com.hanghae.finalp.entity.dto.MemberDto;
import com.hanghae.finalp.service.OauthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final OauthService oauthService;

    /**
     * 카카오 로그인 API
     */
    @GetMapping("/login/oauth2/{provider}")
    @ResponseBody
    public ResponseEntity<ResultMsg> loginOAuth(@PathVariable("provider") String provider, @RequestParam String code) {
        return oauthService.login(provider, code);
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
