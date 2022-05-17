package com.hanghae.finalp.config.exception.advice;

import com.hanghae.finalp.config.exception.customexception.AuthorityException;
import com.hanghae.finalp.config.exception.dto.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
@Order(5000)  //가장 마지막 advice
public class OtherControllerAdvice {

    private final MessageSource ms;

    @ExceptionHandler(AuthorityException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse authorityException(AuthorityException e) {
        log.info("권한관련 에러 발생 {}", e.getMessage());
        return new ErrorResponse(ms.getMessage(e.getCode(), null, null));
    }


    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse httpRequestMethodNotSupportedException(Exception e) {
        log.info("서버에서 지원하지 않는 http 메서드 입니다. {}", e.getMessage());
        e.printStackTrace();
        return new ErrorResponse(ms.getMessage("error.notSupport", null, null));
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse exception(Exception e) {
        log.info("예상치 못한 에러 발생!!! {}", e.getMessage());
        e.printStackTrace();
        return new ErrorResponse(ms.getMessage("error.response", null, null));
    }


}
