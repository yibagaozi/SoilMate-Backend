package com.soilmate.webservice.entity;

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
public class UserPlant {

    /**
     * The unique identifier for this user plant record.
     */
    private Long id;

    /**
     * The ID of the user who owns this plant.
     */
    private Long userId;

    /**
     * The ID of the plant species type.
     */
    private Long plantId;

    /**
     * The user's custom nickname for this plant.
     */
    private String nickname;

    /**
     * The location where this plant is placed.
     */
    private String location;

    /**
     * The URL of the user's photo of this plant.
     */
    private String photoUrl;

    /**
     * The date when this plant was added to the user's garden.
     */
    private LocalDate createDate;

    /**
     * The timestamp of the last watering activity for this plant.
     */
    private LocalDateTime lastWateredAt;

    /**
     * The timestamp of the last feeding (fertilizing) activity for this plant.
     */
    private LocalDateTime lastFedAt;

    /**
     * User notes about this plant.
     */
    private String notes;

    /**
     * Whether this plant is currently active in the user's garden.
     */
    @Builder.Default
    private Boolean isActive = true;

    /**
     * Custom watering interval in days, overriding the plant species default.
     */
    private Integer customWateringIntervalDays;

    /**
     * Custom watering amount in milliliters, overriding the plant species default.
     */
    private Integer customWateringAmountMl;

    /**
     * Custom feeding interval in days, overriding the plant species default.
     */
    private Integer customFeedingIntervalDays;

    /**
     * The timestamp when this user plant record was created.
     */
    private LocalDateTime createdAt;

    /**
     * The timestamp when this user plant record was last updated.
     */
    private LocalDateTime updatedAt;
}
