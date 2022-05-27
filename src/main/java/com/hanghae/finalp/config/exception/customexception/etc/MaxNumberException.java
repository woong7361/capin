package com.hanghae.finalp.config.exception.customexception.etc;

import com.hanghae.finalp.config.exception.code.ErrorCode;
import com.hanghae.finalp.config.exception.customexception.CustomException;
import lombok.Getter;

@Getter
public class MaxNumberException extends CustomException {

    public MaxNumberException(){
        super(ErrorCode.MAX_MEMBER_EXCEPTION);

    }
}

