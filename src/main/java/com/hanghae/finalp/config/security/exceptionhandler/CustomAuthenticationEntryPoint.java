package com.hanghae.finalp.config.security.exceptionhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae.finalp.config.exception.dto.ResultMsg;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final MessageSource messageSource;
    @Override
    @ExceptionHandler(BadCredentialsException.class)
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {


        authException.printStackTrace();
        log.info("CustomAuthenticationEntryPoint Error exception");

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.FORBIDDEN.value());

        if (request.getAttribute("error").equals("accessTokenExpire")) {
            ResultMsg msg = new ResultMsg("accessTokenRequest", "access token expire please throw refresh token");

            try (OutputStream os = response.getOutputStream()) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.writeValue(os, msg);
                os.flush();
            }
        } else {
            //front가 fail만 요청
            ResultMsg msg = new ResultMsg("fail", "entrypoint error");

            try (OutputStream os = response.getOutputStream()) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.writeValue(os, msg);
                os.flush();
            }
        }
    }
}