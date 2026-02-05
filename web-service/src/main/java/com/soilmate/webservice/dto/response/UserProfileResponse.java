package com.soilmate.webservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.soilmate.webservice.entity.User;
import com.soilmate.webservice.entity.UserAuth;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Response DTO for user profile with full details.
 *
 * @author MA, Ruize
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileResponse {

    private Long id;
    private String email;
    private String displayName;
    private String avatarUrl;
    private String timezone;
    private Boolean notificationEnabled;
    private LocalTime reminderTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<AuthMethodResponse> authMethods;
    private UserStatsResponse stats;

    /**
     * Convert UserEntity to UserProfileResponse with auth methods and stats.
     */
    public static UserProfileResponse fromEntity(User entity,
                                                 List<UserAuth> authEntities,
                                                 Integer totalPlants,
                                                 Integer activePlants) {
        List<AuthMethodResponse> authMethods = authEntities.stream()
                .map(auth -> AuthMethodResponse.builder()
                        .provider(auth.getAuthProvider().getCode())
                        .email(auth.getProviderEmail())
                        .isPrimary(auth.getIsPrimary())
                        .build())
                .toList();

        return UserProfileResponse.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .displayName(entity.getDisplayName())
                .avatarUrl(entity.getAvatarUrl())
                .timezone(entity.getTimezone())
                .notificationEnabled(entity.getNotificationEnabled())
                .reminderTime(entity.getReminderTime())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .authMethods(authMethods)
                .stats(UserStatsResponse.builder()
                        .totalPlants(totalPlants)
                        .activePlants(activePlants)
                        .build())
                .build();
    }

    /**
     * Convert from entity only.
     */
    public static UserProfileResponse fromEntity(User entity) {
        return UserProfileResponse.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .displayName(entity.getDisplayName())
                .avatarUrl(entity.getAvatarUrl())
                .timezone(entity.getTimezone())
                .notificationEnabled(entity.getNotificationEnabled())
                .reminderTime(entity.getReminderTime())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
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
