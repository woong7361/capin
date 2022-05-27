package com.hanghae.finalp.config.exception.customexception.authority;

import com.hanghae.finalp.config.exception.code.ErrorCode;
import com.hanghae.finalp.config.exception.customexception.CustomException;
import lombok.Getter;

@Getter
public class AuthorityException extends CustomException {
    public AuthorityException() {
        super(ErrorCode.AUTHORITY_EXCEPTION);
    }

    public AuthorityException(ErrorCode errorCode) {
        super(errorCode);
    }
}
