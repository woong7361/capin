package com.hanghae.finalp.config.exception.customexception;

import com.hanghae.finalp.config.exception.code.ErrorCode;

public class WebClientException extends CustomException{
    public WebClientException(ErrorCode errorCode) {
        super(ErrorCode.WEB_CLIENT_EXCEPTION);
    }
}
