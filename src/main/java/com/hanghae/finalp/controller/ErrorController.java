package com.hanghae.finalp.controller;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.hanghae.finalp.config.exception.customexception.token.TokenException;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@Hidden
public class ErrorController {

    //filter 부분에서 일어난 exception을 통합적으로 관리하기 위하여 redirect 시켰다.
    //=======================================login exception part===================================//
    /**
     * JWT 토큰 관련 에러
     */
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/error/login/tokenException")
    public void tokenException() {
        throw new TokenException();
    }

    /**
     * JWT Access 토큰 만료 에러
     */
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/error/login/tokenExpiredException")
    public void tokenExpiredException() {
        throw new TokenExpiredException("access 토큰 만료");
    }

    /**
     * 일반적인 로그인 에러
     */
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/error/login/exception")
    public void loginException() {
        //그냥 badCredintailExcecption사용
        throw new BadCredentialsException("login error");
    }

}
