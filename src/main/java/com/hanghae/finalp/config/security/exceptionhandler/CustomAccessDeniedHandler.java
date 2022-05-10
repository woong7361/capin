package com.hanghae.finalp.config.security.exceptionhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae.finalp.config.exception.dto.ResultMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.error("사용되지 않는 핸들러입니다. 사용되면 조치하세요!");
        log.error("CustomAccessDeniedHandler Error exception = {}", accessDeniedException.getClass());
        log.error("CustomAccessDeniedHandler Error Exception message = {}", accessDeniedException.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.FORBIDDEN.value());
        ResultMsg error = new ResultMsg("fail", "서버에 문의 가능");

        try (OutputStream os = response.getOutputStream()) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(os, error);
            os.flush();
        }
    }
}