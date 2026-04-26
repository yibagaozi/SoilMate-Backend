package com.soilmate.webservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.soilmate.webservice.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Response DTO for user information.
 *
 * @author MA, Ruize
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

    private Long id;
    private String email;
    private String displayName;
    private String avatarUrl;
    private String timezone;
    private Boolean notificationEnabled;
    private LocalTime reminderTime;
    private LocalDateTime createdAt;
    private List<AuthMethodResponse> authMethods;
    private UserStatsResponse stats;

    /**
     * Convert UserEntity to UserResponse (basic info).
     */
    public static UserResponse fromEntity(User entity) {
        return UserResponse.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .displayName(entity.getDisplayName())
                .avatarUrl(entity.getAvatarUrl())
                .timezone(entity.getTimezone())
                .notificationEnabled(entity.getNotificationEnabled())
                .reminderTime(entity.getReminderTime())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthMethodResponse {
        private String provider;
        private String email;
        private Boolean isPrimary;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserStatsResponse {
        private Integer totalPlants;
        private Integer activePlants;
    }
}
