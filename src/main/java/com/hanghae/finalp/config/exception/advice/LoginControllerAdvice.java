package com.hanghae.finalp.config.exception.advice;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.hanghae.finalp.config.exception.customexception.token.RefreshTokenException;
import com.hanghae.finalp.config.exception.customexception.token.TokenException;
import com.hanghae.finalp.config.exception.dto.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class LoginControllerAdvice {

    private final MessageSource ms;

    @ExceptionHandler(TokenExpiredException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse tokenExpiredException(TokenExpiredException e) {
        return new ErrorResponse("request refresh token");
    }

    @ExceptionHandler(TokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse tokenException(TokenException e) {
        return new ErrorResponse("token exception");
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse loginException(BadCredentialsException e) {
        return new ErrorResponse("login exception");
    }

    @ExceptionHandler(RefreshTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse refreshTokenException(RefreshTokenException e) {
        log.info("refresh token Exception! {}", e.getErrorCode().getMessage());
        log.info("print", e);
        return new ErrorResponse(ms.getMessage(e.getErrorCode().getCode(), null, null));
    }
}

