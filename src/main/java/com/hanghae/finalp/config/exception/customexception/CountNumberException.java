package com.hanghae.finalp.config.exception.customexception;

import lombok.Getter;

@Getter
public class CountNumberException extends RuntimeException {
    private String code;

    public CountNumberException(String code, String message){
        super(message);
        this.code = code;
    }
}

