package com.soilmate.webservice.service;

import com.soilmate.webservice.dto.request.CompleteTaskRequest;
import com.soilmate.webservice.dto.request.SkipTaskRequest;
import com.soilmate.webservice.dto.response.CareTaskItemResponse;
import com.soilmate.webservice.dto.response.TaskDetailResponse;

import java.time.LocalDate;
import java.util.List;

public interface CareTaskService {

    List<CareTaskItemResponse> getTodayTasks(Long userId);

    List<CareTaskItemResponse> getUpcomingTasks(Long userId);

    List<CareTaskItemResponse> getTasksByDate(Long userId, LocalDate date);

    TaskDetailResponse completeTask(Long userId, Long taskId, CompleteTaskRequest request);

    TaskDetailResponse skipTask(Long userId, Long taskId, SkipTaskRequest request);

    void createInitialTasks(Long userPlantId, Integer wateringInterval, Integer wateringAmount, Integer feedingInterval,
                            String feedingType);

    void deleteTasksByUserPlantId(Long userPlantId);

}
