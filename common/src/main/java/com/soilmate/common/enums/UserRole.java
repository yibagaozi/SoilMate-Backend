package com.soilmate.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.soilmate.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * User roles for authorization control.
 *
 * @author MA, Ruize
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum UserRole {

    USER("USER"),
    ADMIN("ADMIN");

    @EnumValue
    private final String code;

    public static UserRole fromCode(String code) {
        if (code == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        for (UserRole role : values()) {
            if (role.code.equalsIgnoreCase(code)) {
                return role;
            }
        }
        throw new BusinessException(ErrorCode.PARAM_ERROR);
    }
}
