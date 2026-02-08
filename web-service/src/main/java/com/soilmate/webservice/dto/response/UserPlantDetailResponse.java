package com.soilmate.webservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.soilmate.common.enums.CareType;
import com.soilmate.common.enums.SunlightRequirement;
import com.soilmate.webservice.entity.CareLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserPlantDetailResponse {

    private Long id;

    private String nickname;

    private String location;

    private String photoUrl;

    private String notes;

    private LocalDateTime lastWateredAt;

    private LocalDateTime lastFedAt;

    private LocalDateTime createdAt;

    private Long plantId;

    private String plantName;

    private String scientificName;

    private SunlightRequirement sunlightRequirement;

    private String plantImageUrl;

    private Integer wateringIntervalDays;

    private Integer wateringAmountMl;

    private Integer feedingIntervalDays;

    private String feedingType;

    private LocalDate nextWateringDate;

    private LocalDate nextFeedingDate;

    private List<CareLog> recentCareLogs;

}
