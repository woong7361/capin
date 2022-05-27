package com.hanghae.finalp.config.exception.customexception.authority;

import com.hanghae.finalp.config.exception.code.ErrorCode;

public class AuthorOwnerException extends AuthorityException{
    public AuthorOwnerException() {
        super(ErrorCode.AUTHORITY_OWNER_EXCEPTION);
    }

}
