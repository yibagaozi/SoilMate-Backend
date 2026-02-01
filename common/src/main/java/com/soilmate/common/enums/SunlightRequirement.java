package com.soilmate.common.enums;

import com.soilmate.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enumeration of sunlight requirement levels for plants.
 *
 * @author MA, Ruize
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum SunlightRequirement {

    LOW("LOW"),
    MEDIUM("MEDIUM"),
    HIGH("HIGH");

    private final String code;

    public static SunlightRequirement fromCode(String code) {
        if (code == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        for (SunlightRequirement requirement : values()) {
            if (requirement.code.equalsIgnoreCase(code)) {
                return requirement;
            }
        }
        throw new BusinessException(ErrorCode.PARAM_ERROR);
    }
}
