package com.soilmate.webservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.soilmate.common.enums.CareType;
import com.soilmate.common.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Response for care task detail.
 *
 * @author MA, Ruize
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CareTaskResponse {

    private Long id;

    private CareType careType;

    private LocalDate scheduledDate;

    private LocalTime scheduledTime;

    private Integer waterAmountMl;

    private String feedingType;

    private String status;  // Display status: PENDING/OVERDUE/COMPLETED/SKIPPED

    private TaskStatus dbStatus;  // Original database status

    private LocalDateTime completedAt;

    private String calendarEventId;

    private UserPlantSummary userPlant;

    private Integer daysUntil;  // Negative if overdue

    private Boolean isOverdue;

    /**
     * Embedded user plant summary.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserPlantSummary {
        private Long id;
        private String nickname;
        private String photoUrl;
        private PlantSummary plant;
    }

    /**
     * Embedded plant summary.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlantSummary {
        private Long id;
        private String name;
    }
}
