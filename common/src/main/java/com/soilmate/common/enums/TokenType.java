package com.soilmate.common.enums;

import com.soilmate.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enumeration of JWT token types used in the authentication system.
 *
 * @author MA, Ruize
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum TokenType {

    ACCESS("access"),
    REFRESH("refresh");

    private final String code;

    public boolean isAccessToken() {
        return this == ACCESS;
    }

    public boolean isRefreshToken() {
        return this == REFRESH;
    }

    public static TokenType fromCode(String code) {
        if (code == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        for (TokenType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new BusinessException(ErrorCode.PARAM_ERROR);
    }
}
