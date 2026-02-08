package com.soilmate.webservice.dto.response;

import com.soilmate.webservice.entity.CareTask;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareTaskListResponse {

    private LocalDate queryDate;

    private List<CareTask> tasks;

    private Integer totalCount;

    private Integer overdueCount;

}
