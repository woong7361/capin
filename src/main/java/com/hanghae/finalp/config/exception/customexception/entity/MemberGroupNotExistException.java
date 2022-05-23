package com.hanghae.finalp.config.exception.customexception.entity;

import com.hanghae.finalp.config.exception.code.ErrorCode;

public class MemberGroupNotExistException extends EntityNotExistException{
    public MemberGroupNotExistException() {
        super(ErrorCode.MEMBER_GROUP_NOT_EXIST);
    }
}
