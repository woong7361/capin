package com.hanghae.finalp.config.exception.customexception;

import com.hanghae.finalp.config.exception.code.ErrorCode;
import lombok.Getter;

@Getter
public class DuplicationRequestException extends CustomException {

    String field;

    public DuplicationRequestException(String field) {
        super(ErrorCode.DUPLICATE_REQUSET);
        this.field = field;
    }
}
