package com.hanghae.finalp.config.exception.customexception.entity;

import com.hanghae.finalp.config.exception.code.ErrorCode;

public class MemberNotExistException extends EntityNotExistException{
    public MemberNotExistException() {
        super(ErrorCode.MEMBER_NOT_EXIST);
    }
}
