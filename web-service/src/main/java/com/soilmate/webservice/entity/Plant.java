package com.soilmate.webservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.soilmate.common.enums.SunlightRequirement;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Plant entity - master catalog of plant species.
 *
 * @author MA, Ruize
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("plant")
public class Plant {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String scientificName;

    private Integer wateringIntervalDays;

    private Integer wateringAmountMl;

    private Integer feedingIntervalDays;

    private String feedingType;

    private SunlightRequirement sunlightRequirement;

    private String imageUrl;

    private String note;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

}
