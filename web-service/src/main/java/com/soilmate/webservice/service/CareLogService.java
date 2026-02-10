package com.soilmate.webservice.service;

import com.soilmate.webservice.dto.request.AddCareLogRequest;
import com.soilmate.webservice.dto.response.CareLogItemResponse;

import java.time.LocalDate;
import java.util.List;

public interface CareLogService {

    List<CareLogItemResponse> getLogsByDate(Long userId, LocalDate date, Long userPlantId);

    List<CareLogItemResponse> getRecentLogs(Long userPlantId, int limit);

    CareLogItemResponse addCareLog(Long userId, AddCareLogRequest request);

    void deleteLogsByUserPlantId(Long userPlantId);
}
