package com.hanghae.finalp.config.exception.advice;


import com.hanghae.finalp.config.exception.customexception.S3Exception;
import com.hanghae.finalp.config.exception.dto.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
@Order(10)
public class S3ControllerAdvice {

    private final MessageSource ms;

    @ExceptionHandler(S3Exception.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public ErrorResponse S3Exception(S3Exception e) {
        log.info("S3Exception - S3 업로딩/ 다운로딩 에러 -> {}", e.getErrorCode().getMessage());
        return new ErrorResponse(ms.getMessage(e.getErrorCode().getCode(), null, null));
    }

}
