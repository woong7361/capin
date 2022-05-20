package com.hanghae.finalp.config.exception.customexception;

import lombok.Getter;

@Getter
public class MaxNumberException extends RuntimeException {
    private String code;

    public MaxNumberException(String code, String message){
        super(message);
        this.code = code;
    }
}

