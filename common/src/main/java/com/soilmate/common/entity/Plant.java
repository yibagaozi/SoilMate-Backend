package com.soilmate.common.entity;

import com.soilmate.common.enums.SunlightRequirement;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Plant {

    /**
     * The unique identifier for this plant species.
     */
    private Long id;

    /**
     * The common name of the plant.
     */
    private String name;

    /**
     * The scientific (botanical) name of the plant.
     */
    private String scientificName;

    /**
     * The recommended number of days between watering.
     */
    private Integer wateringIntervalDays;

    /**
     * The recommended amount of water per watering session in milliliters.
     */
    private Integer wateringAmountMl;

    /**
     * The recommended number of days between fertilizer applications.
     */
    private Integer feedingIntervalDays;

    /**
     * The recommended type of fertilizer for this plant.
     */
    private String feedingType;

    /**
     * The light requirement level for optimal plant growth.
     */
    private SunlightRequirement sunlightRequirement;

    /**
     * The URL of the default image for this plant species.
     */
    private String imageUrl;

    /**
     * The timestamp when this plant record was created.
     */
    private LocalDateTime createdAt;

    /**
     * The timestamp when this plant record was last updated.
     */
    private LocalDateTime updatedAt;

}
