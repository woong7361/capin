package com.hanghae.finalp.config.exception.customexception.etc;

import com.hanghae.finalp.config.exception.code.ErrorCode;
import com.hanghae.finalp.config.exception.customexception.CustomException;

public class WebClientException extends CustomException {
    public WebClientException() {
        super(ErrorCode.WEB_CLIENT_EXCEPTION);
    }
}
