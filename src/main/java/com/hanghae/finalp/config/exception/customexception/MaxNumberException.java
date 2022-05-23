package com.hanghae.finalp.config.exception.customexception;

import com.hanghae.finalp.config.exception.code.ErrorCode;
import lombok.Getter;

@Getter
public class MaxNumberException extends CustomException {

    public MaxNumberException(){
        super(ErrorCode.MAX_MEMBER_EXCEPTION);

    }
}

