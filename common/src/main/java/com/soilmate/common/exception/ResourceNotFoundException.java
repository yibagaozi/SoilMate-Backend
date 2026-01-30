package com.soilmate.common.exception;

import com.soilmate.common.enums.ErrorCode;

public class ResourceNotFoundException extends BaseException {

    public ResourceNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ResourceNotFoundException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

}
