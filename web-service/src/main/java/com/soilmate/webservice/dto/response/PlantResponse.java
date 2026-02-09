package com.soilmate.webservice.dto.response;

import com.soilmate.common.enums.SunlightRequirement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlantResponse {

    private Long id;

    private String name;

    private String scientificName;

    private Integer wateringIntervalDays;

    private Integer wateringAmountMl;

    private Integer feedingIntervalDays;

    private String feedingType;

    private SunlightRequirement sunlightRequirement;

    private String imageUrl;
}
