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
public class CareLogListResponse {

    private LocalDate queryDate;

    private List<CareLog> logs;

    private Integer totalCount;

}
