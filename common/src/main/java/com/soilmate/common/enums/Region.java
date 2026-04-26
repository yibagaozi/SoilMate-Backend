package com.soilmate.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.soilmate.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Region {

    TOKYO("TOKYO"),
    BEIJING("BEIJING"),
    SHANGHAI("SHANGHAI"),
    HONG_KONG("HONG_KONG"),
    SINGAPORE("SINGAPORE"),
    LONDON("LONDON"),
    NEW_YORK("NEW_YORK"),
    LOS_ANGELES("LOS_ANGELES"),
    SYDNEY("SYDNEY"),
    DUBAI("DUBAI");

    @EnumValue
    private final String code;

    public static Region fromCode(String code) {
        if (code == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        for (Region region : values()) {
            if (region.code.equalsIgnoreCase(code)) {
                return region;
            }
        }
        throw new BusinessException(ErrorCode.PARAM_ERROR);
    }
}
