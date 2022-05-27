package com.hanghae.finalp.config.exception.customexception.authority;

import com.hanghae.finalp.config.exception.code.ErrorCode;

public class AuthorWaitException extends AuthorityException{
    public AuthorWaitException() {
        super(ErrorCode.AUTHORITY_WAIT_EXCEPTION);
    }

}
