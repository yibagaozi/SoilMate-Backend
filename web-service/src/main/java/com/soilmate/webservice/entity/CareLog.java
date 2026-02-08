package com.soilmate.webservice.entity;

import com.baomidou.mybatisplus.annotation.*;
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
@TableName("care_log")
public class CareLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userPlantId;

    private CareType careType;

    private LocalDateTime performedAt;

    private Integer waterAmountMl;

    private String feedingType;

    private String notes;

    private String photoUrl;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}