package com.hanghae.finalp.config.exception.customexception;

import lombok.Getter;

@Getter
public class S3Exception extends RuntimeException{

    private String code;

    public S3Exception(String code, String message) {
        super(message);
        this.code = code;
    }
}
