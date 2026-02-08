package com.soilmate.webservice.entity;

import com.baomidou.mybatisplus.annotation.*;
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
@TableName("care_task")
public class CareTask {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userPlantId;

    private CareType careType;

    private LocalDate scheduledDate;

    private LocalTime scheduledTime;

    private Integer waterAmountMl;

    private String feedingType;

    @Builder.Default
    private TaskStatus status = TaskStatus.PENDING;

    private LocalDateTime completedAt;

    private String calendarEventId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
