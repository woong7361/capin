package com.hanghae.finalp.config.exception.advice;

import com.hanghae.finalp.config.exception.customexception.etc.DuplicationRequestException;
import com.hanghae.finalp.config.exception.customexception.authority.AuthorityException;
import com.hanghae.finalp.config.exception.customexception.etc.MaxNumberException;
import com.hanghae.finalp.config.exception.dto.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
@Order(5000)  //가장 마지막 advice
public class OtherControllerAdvice {

    private final MessageSource ms;

    @ExceptionHandler(AuthorityException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse authorityException(AuthorityException e) {
        log.info("권한관련 에러 발생 {}", e.getErrorCode().getMessage());
        e.printStackTrace();
        return new ErrorResponse(ms.getMessage(e.getErrorCode().getCode(), null, null));
    }


    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse httpRequestMethodNotSupportedException(Exception e) {
        log.info("서버에서 지원하지 않는 http 메서드 입니다. {}", e.getMessage());
        e.printStackTrace();
        return new ErrorResponse(ms.getMessage("error.notSupport", null, null));
    }


    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse BindException(BindException e) {
        log.info("@Valid error -> 필드 바인드 에러 입니다. {}", e.getMessage());
        List<FieldError> fieldErrors = e.getFieldErrors();
        String message = fieldErrors.stream()
                .map(f -> f.getField() + "__" + f.getDefaultMessage()).collect(Collectors.toList()).toString();
        log.info("field name + __ + filed error message = {}", message);
        return new ErrorResponse(ms.getMessage("error.field", null, null));
    }


    @ExceptionHandler(MaxNumberException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse maxNumberException(MaxNumberException e) {
        log.info("최대 인원수 에러입니다  {}", e.getErrorCode().getMessage());
        e.printStackTrace();
        return new ErrorResponse(ms.getMessage(e.getErrorCode().getCode(), null, null));
    }

    @ExceptionHandler(DuplicationRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse duplicationRequestException(DuplicationRequestException e) {
        log.info("중복된 요청 오류입니다.{}, field: {}", e.getErrorCode().getMessage(), e.getField());
        e.printStackTrace();
        return new ErrorResponse(ms.getMessage(e.getErrorCode().getCode(), null, null));
    }




    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse exception(Exception e) {
        log.info("예상치 못한 에러 발생!!! {}", e.getMessage());
        e.printStackTrace();
        return new ErrorResponse(ms.getMessage("error.response", null, null));
    }


}
