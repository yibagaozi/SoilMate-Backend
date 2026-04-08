package com.soilmate.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.soilmate.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GrowingEnvironment {

    Indoor("INDOOR"),
    Outdoor("OUTDOOR"),
    Greenhouse("GREENHOUSE");

    @EnumValue
    private final String code;

    public static GrowingEnvironment fromCode(String code) {
        if (code == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        for (GrowingEnvironment environment : values()) {
            if (environment.code.equalsIgnoreCase(code)) {
                return environment;
            }
        }
        throw new BusinessException(ErrorCode.PARAM_ERROR);
    }

}
