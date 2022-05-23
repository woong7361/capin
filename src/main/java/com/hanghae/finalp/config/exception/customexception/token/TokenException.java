package com.hanghae.finalp.config.exception.customexception.token;

import com.hanghae.finalp.config.exception.code.ErrorCode;
import com.hanghae.finalp.config.exception.customexception.CustomException;
import lombok.Getter;

@Getter
public class TokenException extends CustomException {

    public TokenException() {
        super(ErrorCode.TOKEN_EXCEPTION);

    }
}
