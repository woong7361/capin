package com.hanghae.finalp.controller;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.hanghae.finalp.config.exception.code.ErrorMessageCode;
import com.hanghae.finalp.config.exception.customexception.TokenException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ErrorController {

    /**
     * login controller exception
     */
    @GetMapping("/error/login/tokenExpiredException")
    public void tokenExpiredException() {
        throw new TokenExpiredException("access 토큰 만료");
    }
    @GetMapping("/error/login/tokenException")
    public void tokenException() {
        throw new TokenException(ErrorMessageCode.TOKEN_ERROR_CODE, "token error");
    }
    @GetMapping("/error/login/exception")
    public void loginException() {
        //그냥 badCredintailExcecption사용
        throw new BadCredentialsException("login error");
    }



}
