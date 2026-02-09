package com.soilmate.webservice.dto.response;

import com.soilmate.common.enums.CareType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareTaskItemResponse {

    private Long id;

    private CareType careType;

    private LocalDate scheduledDate;

    private String status;

    private Integer daysUntil;

    private Integer waterAmountMl;

    private String feedingType;

    private Long userPlantId;

    private String plantNickname;

    private String plantImageUrl;

}
