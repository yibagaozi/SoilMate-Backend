package com.soilmate.webservice.service.impl;

import com.soilmate.common.security.dto.TokenResponse;
import com.soilmate.webservice.dto.request.LoginRequest;
import com.soilmate.webservice.dto.request.RefreshTokenRequest;
import com.soilmate.webservice.dto.request.RegisterRequest;
import com.soilmate.webservice.dto.response.AuthResponse;
import com.soilmate.webservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Override
    public AuthResponse register(RegisterRequest request) {
        return null;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        return null;
    }

    @Override
    public void logout(Long userId) {

    }

    @Override
    public TokenResponse refreshToken(RefreshTokenRequest request) {
        return null;
    }
}
