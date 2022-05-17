package com.hanghae.finalp.config.exception.customexception;

import lombok.Getter;

@Getter
public class TokenException extends RuntimeException {

    private String code;

    public TokenException(String code, String message) {
        super(message);
        this.code = code;
    }
}
