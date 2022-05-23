package com.hanghae.finalp.config.exception.customexception.authority;

import com.hanghae.finalp.config.exception.code.ErrorCode;
import lombok.Getter;

@Getter
public class AuthorJoinException extends AuthorityException{
    public AuthorJoinException() {
        super(ErrorCode.AUTHORITY_JOIN_EXCEPTION);
    }
}
