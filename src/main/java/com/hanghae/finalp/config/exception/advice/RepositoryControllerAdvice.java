package com.hanghae.finalp.config.exception.advice;


import com.auth0.jwt.exceptions.TokenExpiredException;
import com.hanghae.finalp.config.exception.customexception.EntityNotExistException;
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
public class RepositoryControllerAdvice {

    private final MessageSource ms;

    @ExceptionHandler(EntityNotExistException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse entityNotExistException(EntityNotExistException e) {
        log.info("entityNotExistException - DB에서 조회 에러 -> {}", e.getMessage());
        return new ErrorResponse(ms.getMessage(e.getCode(), null, null));
    }

}
