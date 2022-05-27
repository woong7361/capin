package com.hanghae.finalp.config.exception.customexception.etc;

import com.hanghae.finalp.config.exception.code.ErrorCode;
import com.hanghae.finalp.config.exception.customexception.CustomException;
import lombok.Getter;

@Getter
public class DuplicationRequestException extends CustomException {

    String field;

    public DuplicationRequestException(String field) {
        super(ErrorCode.DUPLICATE_REQUSET);
        this.field = field;
    }
}
