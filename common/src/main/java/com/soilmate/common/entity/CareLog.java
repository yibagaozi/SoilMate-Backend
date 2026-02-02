package com.soilmate.common.entity;

import com.soilmate.common.enums.CareType;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity representing the care activity history log.
 *
 * @author MA, Ruize
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareLog {

    /**
     * The unique identifier for this care log entry.
     */
    private Long id;

    /**
     * The ID of the user plant this log entry is for.
     */
    private Long userPlantId;

    /**
     * The type of care activity that was performed.
     */
    private CareType careType;

    /**
     * The timestamp when the care activity was performed.
     */
    private LocalDateTime performedAt;

    /**
     * The actual amount of water given in milliliters.
     */
    private Integer waterAmountMl;

    /**
     * The type of fertilizer that was used.
     */
    private String feedingType;

    /**
     * Optional notes about this care activity.
     */
    private String notes;

    /**
     * The URL of a photo taken during this care activity.
     */
    private String photoUrl;

    /**
     * The timestamp when this log entry was created.
     */
    private LocalDateTime createdAt;
}