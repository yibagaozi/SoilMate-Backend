package com.soilmate.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.soilmate.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enumeration of care type for plant tasks.
 *
 * @author MA, Ruize
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum CareType {

    WATER("WATER"),
    FEED("FEED");

    @EnumValue
    private final String code;

    public static CareType fromCode(String code) {
        if (code == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        for (CareType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new BusinessException(ErrorCode.PARAM_ERROR);
    }
}
