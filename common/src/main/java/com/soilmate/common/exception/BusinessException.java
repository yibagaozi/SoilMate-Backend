package com.soilmate.common.exception;

import com.soilmate.common.enums.ErrorCode;

public class BusinessException extends BaseException {

    public BusinessException(ErrorCode errorCode) {
        super(errorCode);
    }

    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

}
