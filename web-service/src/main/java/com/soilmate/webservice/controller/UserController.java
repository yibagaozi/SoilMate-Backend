package com.soilmate.webservice.controller;

import com.soilmate.common.response.ApiResponse;
import com.soilmate.common.security.annotation.RequireAuth;
import com.soilmate.webservice.dto.request.UpdateUserRequest;
import com.soilmate.webservice.dto.response.UserProfileResponse;
import com.soilmate.webservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for user profile endpoints.
 *
 * @author MA, Ruize
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@RequireAuth
public class UserController {

    private final UserService userService;

    /**
     * Get current user's profile.
     * GET /users/me
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMyProfile() {
        log.info("Get my profile request received");

        UserProfileResponse response = userService.getMyProfile();

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Update current user's profile.
     * PATCH /users/me
     */
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateMyProfile(
            @Valid @RequestBody UpdateUserRequest request) {
        log.info("Update my profile request received");

        UserProfileResponse response = userService.updateMyProfile(request);

        return ResponseEntity.ok(ApiResponse.success(response, "Profile updated"));
    }
}
