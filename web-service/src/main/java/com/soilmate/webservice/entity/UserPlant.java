package com.soilmate.webservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.soilmate.common.enums.GrowingEnvironment;
import com.soilmate.common.enums.PotSize;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing the user's plant collection table.
 *
 * @author MA, Ruize
 * @since 1.0.0
 * @see User
 * @see Plant
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_plant")
public class UserPlant {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long plantId;

    private String nickname;

    private String location;

    private String imageUrl;

    private String notes;

    @TableField("is_active")
private Boolean active = true;

    private LocalDateTime lastWateredAt;

    private LocalDateTime lastFedAt;

    private Integer customWateringIntervalDays;

    private Integer customWateringAmountMl;

    private Integer customFeedingIntervalDays;

    private String customFeedingType;

    private PotSize potSize;

    private GrowingEnvironment environment;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
