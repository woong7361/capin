package com.hanghae.finalp.config.exception.customexception.token;

import com.hanghae.finalp.config.exception.code.ErrorCode;
import com.hanghae.finalp.config.exception.customexception.CustomException;
import lombok.Getter;

@Getter
public class RefreshTokenException extends CustomException {

    public RefreshTokenException() {
        super(ErrorCode.REFRESH_TOKEN_EXCEPTION);
    }
}
