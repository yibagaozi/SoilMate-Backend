package com.soilmate.common.exception;

import com.soilmate.common.enums.ErrorCode;

public class TokenException extends BaseException {
    public TokenException(ErrorCode errorCode) {
        super(errorCode);
    }

    public TokenException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
