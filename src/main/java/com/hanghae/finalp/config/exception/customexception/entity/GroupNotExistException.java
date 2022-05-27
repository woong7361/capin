package com.hanghae.finalp.config.exception.customexception.entity;

import com.hanghae.finalp.config.exception.code.ErrorCode;

public class GroupNotExistException extends EntityNotExistException{
    public GroupNotExistException() {
        super(ErrorCode.GROUP_NOT_EXIST);
    }
}
