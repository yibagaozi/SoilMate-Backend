package com.soilmate.webservice.dto.request;

import com.soilmate.common.enums.CareType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AddCareLogRequest {

    private Long userPlantId;

    private CareType careType;

    private LocalDateTime performedAt;

    private Integer waterAmountMl;

    private String feedingType;

    private String notes;
}
