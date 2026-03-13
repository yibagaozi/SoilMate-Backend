package com.soilmate.webservice.dto.response;

import com.soilmate.common.enums.CareType;
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
public class CareLogItemResponse {

    private Long id;

    private Long taskId;

    private CareType careType;

    private LocalDateTime performedAt;

    private Integer waterAmountMl;

    private String feedingType;

    private String notes;

    private Long userPlantId;

    private String plantNickname;

    private String plantImageUrl;

    private String plantLocation;

    private String photoUrl;

}
