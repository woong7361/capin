package com.hanghae.finalp.config.exception.customexception;

import lombok.Getter;

@Getter
public class RefreshTokenException extends RuntimeException{

    private String code;

    public RefreshTokenException(String code, String message) {
        super(message);
        this.code = code;
    }
}
