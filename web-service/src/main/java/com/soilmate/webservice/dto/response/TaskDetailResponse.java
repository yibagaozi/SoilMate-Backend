package com.soilmate.webservice.dto.response;

import com.soilmate.common.enums.CareType;
import com.soilmate.common.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDetailResponse {

    private Long id;

    private CareType careType;

    private LocalDate scheduledDate;

    private Integer waterAmountMl;

    private String feedingType;

    private TaskStatus status;

    private LocalDateTime completedAt;

    private String skipReason;

    private Long nextTaskId;

    private LocalDate nextTaskDate;
}
