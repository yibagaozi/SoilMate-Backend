package com.soilmate.webservice.entity;

import com.soilmate.common.enums.CareType;
import com.soilmate.common.enums.TaskStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Entity representing scheduled care reminder tasks.
 *
 * @author MA, Ruize
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareTask {

    /**
     * The unique identifier for this care task.
     */
    private Long id;

    /**
     * The ID of the user plant this task is for.
     */
    private Long userPlantId;

    /**
     * The type of care activity for this task.
     */
    private CareType careType;

    /**
     * The scheduled date for this care task.
     */
    private LocalDate scheduledDate;

    /**
     * The scheduled time for this care task.
     */
    private LocalTime scheduledTime;

    /**
     * The recommended water amount in milliliters for this task.
     */
    private Integer waterAmountMl;

    /**
     * The recommended fertilizer type for this task.
     */
    private String feedingType;

    /**
     * The current status of this care task.
     */
    @Builder.Default
    private TaskStatus status = TaskStatus.PENDING;

    /**
     * The timestamp when this task was completed or skipped.
     */
    private LocalDateTime completedAt;

    /**
     * The iOS system calendar event identifier.
     */
    private String calendarEventId;

    /**
     * The timestamp when this task record was created.
     */
    private LocalDateTime createdAt;
}
