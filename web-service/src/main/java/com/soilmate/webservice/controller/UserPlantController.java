package com.soilmate.webservice.controller;

import com.soilmate.common.response.ApiResponse;
import com.soilmate.common.security.annotation.RequireAuth;
import com.soilmate.common.security.context.UserContextHolder;
import com.soilmate.webservice.dto.request.AddUserPlantRequest;
import com.soilmate.webservice.dto.request.UpdateUserPlantRequest;
import com.soilmate.webservice.dto.response.UserPlantDetailResponse;
import com.soilmate.webservice.service.UserPlantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for user plant endpoints.
 *
 * @author MA, Ruize
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/user-plants")
@RequiredArgsConstructor
@RequireAuth
public class UserPlantController {

    private final UserPlantService userPlantService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Map<String, Long>> addUserPlant(@RequestBody AddUserPlantRequest request) {
        Long userId = UserContextHolder.getCurrentUserId();
        Long userPlantId = userPlantService.addUserPlant(userId, request);
        return ApiResponse.created(Map.of("id", userPlantId), "Plant added");
    }

    @PatchMapping("/{id}")
    public ApiResponse<Void> updateUserPlant(@PathVariable Long id, @RequestBody UpdateUserPlantRequest request) {
        Long userId = UserContextHolder.getCurrentUserId();
        userPlantService.updateUserPlant(userId, id, request);
        return ApiResponse.success(null, "Plant updated");
    }

    @GetMapping
    public ApiResponse<List<UserPlantDetailResponse>> getUserPlants() {
        Long userId = UserContextHolder.getCurrentUserId();
        return ApiResponse.success(userPlantService.getUserPlants(userId));
    }

    @GetMapping("/{id}")
    public ApiResponse<UserPlantDetailResponse> getUserPlantDetail(@PathVariable Long id) {
        Long userId = UserContextHolder.getCurrentUserId();
        return ApiResponse.success(userPlantService.getUserPlantDetail(userId, id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserPlant(@PathVariable Long id) {
        Long userId = UserContextHolder.getCurrentUserId();
        userPlantService.deleteUserPlant(userId, id);
    }
}
