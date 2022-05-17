package com.hanghae.finalp.config.exception.customexception;

public class TokenException extends RuntimeException {

    private String code;

    public TokenException(String code, String message) {
        super(message);
        this.code = code;
    }
}
