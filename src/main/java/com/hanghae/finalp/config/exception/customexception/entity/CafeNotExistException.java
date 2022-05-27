package com.hanghae.finalp.config.exception.customexception.entity;

import com.hanghae.finalp.config.exception.code.ErrorCode;

public class CafeNotExistException extends EntityNotExistException{
    public CafeNotExistException() {
        super(ErrorCode.CAFE_NOT_EXIST);
    }
}
