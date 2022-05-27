package com.hanghae.finalp.config.security.exceptionhandler;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.hanghae.finalp.config.exception.customexception.token.TokenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        log.info("CustomAuthenticationEntryPoint Error exceptionhandler 진입");

        if (request.getAttribute("type").equals("tokenExpiredException")) {
            TokenExpiredException error = (TokenExpiredException) request.getAttribute("error");
            log.info("token 만료됨");
            response.sendRedirect("/error/login/tokenExpiredException");
        } else if (request.getAttribute("type").equals("tokenException")) {
            TokenException error = (TokenException) request.getAttribute("error");
            log.info("TokenException 에러 {}", error.getErrorCode().getMessage());
            response.sendRedirect("/error/login/tokenException");
        } else {
            Exception error = (Exception) request.getAttribute("error");
            error.printStackTrace();
            response.sendRedirect("/error/login/exception");
        }
    }
}