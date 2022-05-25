package com.hanghae.finalp.config.exception.customexception.etc;

import com.hanghae.finalp.config.exception.code.ErrorCode;
import com.hanghae.finalp.config.exception.customexception.CustomException;
import lombok.Getter;

@Getter
public class S3Exception extends CustomException {

    public S3Exception() {
        super(ErrorCode.S3_EXCEPTION);
    }
}
