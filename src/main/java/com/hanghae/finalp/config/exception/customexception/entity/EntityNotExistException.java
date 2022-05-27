package com.hanghae.finalp.config.exception.customexception.entity;


import com.hanghae.finalp.config.exception.code.ErrorCode;
import com.hanghae.finalp.config.exception.customexception.CustomException;
import lombok.Getter;

@Getter
public class EntityNotExistException extends CustomException {
    public EntityNotExistException() {
        super(ErrorCode.ENTITY_NOT_EXIST);
    }

    public EntityNotExistException(ErrorCode errorCode) {
        super(errorCode);
    }
}
