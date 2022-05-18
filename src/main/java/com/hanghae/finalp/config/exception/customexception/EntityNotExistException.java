package com.hanghae.finalp.config.exception.customexception;


import lombok.Getter;

@Getter
public class EntityNotExistException extends RuntimeException{
    private String code;

    public EntityNotExistException(String code, String message) {
        super(message);
        this.code = code;
    }
}
