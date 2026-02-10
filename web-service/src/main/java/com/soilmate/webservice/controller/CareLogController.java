package com.soilmate.webservice.controller;

import com.soilmate.common.response.ApiResponse;
import com.soilmate.common.security.annotation.RequireAuth;
import com.soilmate.common.security.context.UserContextHolder;
import com.soilmate.webservice.dto.request.AddCareLogRequest;
import com.soilmate.webservice.dto.response.CareLogItemResponse;
import com.soilmate.webservice.service.CareLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/care-log")
@RequireAuth
@RequiredArgsConstructor
public class CareLogController {

    private final CareLogService careLogService;

    @GetMapping
    public ApiResponse<List<CareLogItemResponse>> getLogsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Long userPlantId) {
        Long userId = UserContextHolder.getCurrentUserId();
        return ApiResponse.success(careLogService.getLogsByDate(userId, date, userPlantId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CareLogItemResponse> addCareLog(@RequestBody AddCareLogRequest request) {
        Long userId = UserContextHolder.getCurrentUserId();
        return ApiResponse.created(careLogService.addCareLog(userId, request), "Care log added");
    }
}
