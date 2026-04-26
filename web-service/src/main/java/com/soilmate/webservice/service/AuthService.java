package com.soilmate.webservice.service;

import com.soilmate.common.security.dto.TokenResponse;
import com.soilmate.webservice.dto.request.LoginRequest;
import com.soilmate.webservice.dto.request.RefreshTokenRequest;
import com.soilmate.webservice.dto.request.RegisterRequest;
import com.soilmate.webservice.dto.response.AuthResponse;

/**
 * Authentication service interface.
 *
 * @author MA, Ruize
 * @since 1.0.0
 */
public interface AuthService {

    /**
     * Registers a new user with email and password.
     *
     * @param request registration request containing email, password, and display name
     * @return authentication response with user info and tokens
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Authenticates a user with email and password.
     *
     * @param request login request containing email and password
     * @return authentication response with user info and tokens
     */
    AuthResponse login(LoginRequest request);

    /**
     * Logs out the current user.
     * <p>
     * Note: With stateless JWT, this is primarily for client-side token cleanup.
     * Future implementation may include token blacklisting.
     * </p>
     *
     * @param userId the ID of the user to log out
     */
    void logout(Long userId);

    /**
     * Refreshes an expired access token using a valid refresh token.
     *
     * @param request refresh token request
     * @return new token pair (access + refresh)
     */
    TokenResponse refreshToken(RefreshTokenRequest request);
}
