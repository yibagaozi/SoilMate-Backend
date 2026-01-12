package com.soilmate.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    SYSTEM_ERROR("SYSTEM_001", "System Error")

    private final String code;
    private final String message;
}
