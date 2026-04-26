package com.soilmate.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.soilmate.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PotSize {

    SMALL("SMALL"),
    MEDIUM("MEDIUM"),
    LARGE("LARGE"),
    EXTRA_LARGE("EXTRA_LARGE");

    @EnumValue
    private final String code;

    public static PotSize fromCode(String code) {
        if (code == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        for (PotSize potSize : values()) {
            if (potSize.code.equalsIgnoreCase(code)) {
                return potSize;
            }
        }
        throw new BusinessException(ErrorCode.PARAM_ERROR);
    }
}
