package com.soilmate.common.exception;

import com.soilmate.common.enums.ErrorCode;

public class AccessDeniedException extends BaseException {
    public AccessDeniedException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AccessDeniedException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
