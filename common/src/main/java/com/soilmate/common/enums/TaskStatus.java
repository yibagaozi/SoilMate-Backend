package com.soilmate.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.soilmate.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enumeration of care task statuses.
 *
 * @author MA, Ruize
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum TaskStatus {

    PENDING("PENDING", "Pending", "Task is scheduled and waiting to be completed"),
    COMPLETED("COMPLETED", "Completed", "Task has been completed successfully"),
    SKIPPED("SKIPPED", "Skipped", "Task was skipped without completion");

    @EnumValue
    private final String code;
    private final String label;
    private final String description;

    public boolean isPending() {
        return this == PENDING;
    }

    public boolean isCompleted() {
        return this == COMPLETED;
    }

    public boolean isSkipped() {
        return this == SKIPPED;
    }

    public boolean isTerminal() {
        return this == COMPLETED || this == SKIPPED;
    }

    public static TaskStatus fromCode(String code) {
        if (code == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        for (TaskStatus status : values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return status;
            }
        }
        throw new BusinessException(ErrorCode.PARAM_ERROR);
    }
}
