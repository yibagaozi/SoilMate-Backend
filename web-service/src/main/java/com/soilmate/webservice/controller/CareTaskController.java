package com.soilmate.webservice.controller;

import com.soilmate.common.response.ApiResponse;
import com.soilmate.common.security.annotation.RequireAuth;
import com.soilmate.common.security.context.UserContextHolder;
import com.soilmate.webservice.dto.request.CompleteTaskRequest;
import com.soilmate.webservice.dto.request.SkipTaskRequest;
import com.soilmate.webservice.dto.response.CareTaskItemResponse;
import com.soilmate.webservice.dto.response.TaskDetailResponse;
import com.soilmate.webservice.service.CareTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller for care task endpoints.
 *
 * @author MA, Ruize
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/care-task")
@RequiredArgsConstructor
@RequireAuth
public class CareTaskController {

    private final CareTaskService careTaskService;

    @GetMapping("/today")
    public ApiResponse<List<CareTaskItemResponse>> getTodayTasks() {
        Long userId = UserContextHolder.getCurrentUserId();
        return ApiResponse.success(careTaskService.getTodayTasks(userId));
    }

    @GetMapping("/upcoming")
    public ApiResponse<List<CareTaskItemResponse>> getUpcomingTasks() {
        Long userId = UserContextHolder.getCurrentUserId();
        return ApiResponse.success(careTaskService.getUpcomingTasks(userId));
    }

    @GetMapping
    public ApiResponse<List<CareTaskItemResponse>> getTasksByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Long userId = UserContextHolder.getCurrentUserId();
        return ApiResponse.success(careTaskService.getTasksByDate(userId, date));
    }

    @PostMapping("/{id}/complete")
    public ApiResponse<TaskDetailResponse> completeTask(@PathVariable Long id,
                                                        @RequestBody(required = false) CompleteTaskRequest request) {
        Long userId = UserContextHolder.getCurrentUserId();
        if (request == null) {
            request = new CompleteTaskRequest();
        }
        return ApiResponse.success(careTaskService.completeTask(userId, id, request), "Task completed");
    }

    @PostMapping("/{id}/skip")
    public ApiResponse<TaskDetailResponse> skipTask(@PathVariable Long id,
                                                    @RequestBody(required = false) SkipTaskRequest request) {
        Long userId = UserContextHolder.getCurrentUserId();
        if (request == null) {
            request = new SkipTaskRequest();
        }
        return ApiResponse.success(careTaskService.skipTask(userId, id, request), "Task skipped");
    }
}
