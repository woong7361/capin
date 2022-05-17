package com.hanghae.finalp.config.exception.customexception;

import lombok.Getter;

@Getter
public class AuthorityException extends RuntimeException{
    private String code;

    public AuthorityException(String code, String message) {
        super(message);
        this.code = code;
    }
}
