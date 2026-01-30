package com.soilmate.common.exception;

import com.soilmate.common.enums.ErrorCode;

public class BarcodeException extends BaseException {

    public BarcodeException(ErrorCode errorCode) {
        super(errorCode);
    }

    public BarcodeException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

}
