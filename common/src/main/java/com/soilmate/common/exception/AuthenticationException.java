package com.soilmate.common.exception;

import com.soilmate.common.enums.ErrorCode;

public class AuthenticationException extends BaseException {

    public AuthenticationException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AuthenticationException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

}
