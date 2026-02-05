package com.soilmate.webservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.soilmate.common.enums.UserRole;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Entity representing the application user table.
 *
 * @author MA, Ruize
 * @since 1.0.0
 * @see UserAuth
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("user")
public class User {

    /**
     * The unique identifier for this user.
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * The user's primary email address.
     */
    private String email;

    /**
     * The user's display name shown in the application.
     */
    private String displayName;

    /**
     * The URL of the user's profile picture.
     */
    private String avatarUrl;

    /**
     * The user's timezone for scheduling and displaying times.
     */
    @Builder.Default
    private String timezone = "UTC";

    /**
     * Whether push notifications are enabled for this user.
     */
    @Builder.Default
    private Boolean notificationEnabled = true;

    /**
     * The preferred time of day to receive care reminders.
     */
    @Builder.Default
    private LocalTime reminderTime = LocalTime.of(9, 0);

    @Builder.Default
    private UserRole role = UserRole.USER;

    /**
     * The timestamp when this user account was created.
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * The timestamp when this user account was last updated.
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
