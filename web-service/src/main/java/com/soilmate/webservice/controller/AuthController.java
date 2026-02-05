package com.soilmate.webservice.controller;

import com.soilmate.common.response.ApiResponse;
import com.soilmate.webservice.dto.request.RegisterRequest;
import com.soilmate.webservice.dto.response.AuthResponse;
import com.soilmate.webservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for authentication endpoints.
 *
 * @author MA, Ruize
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Register a new user with email and password.
     * POST /auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {

        log.info("Register request received for email: {}", request.getEmail());

        AuthResponse response = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "Registration successful"));
    }

}
