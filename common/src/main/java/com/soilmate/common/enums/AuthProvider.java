package com.soilmate.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.soilmate.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enumeration of supported authentication providers.
 *
 * @author MA, Ruize
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum AuthProvider {

    EMAIL("EMAIL"),
    APPLE("APPLE");

    @EnumValue
    private final String code;

    public boolean isOAuth() {
        return this == APPLE;
    }

    public boolean requiresPassword() {
        return this == EMAIL;
    }

    public static AuthProvider fromCode(String code) {
        if (code == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        for (AuthProvider provider : values()) {
            if (provider.code.equalsIgnoreCase(code)) {
                return provider;
            }
        }
        throw new BusinessException(ErrorCode.PARAM_ERROR);
    }
}
