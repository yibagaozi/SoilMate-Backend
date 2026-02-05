package com.soilmate.webservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.soilmate.common.enums.AuthProvider;
import com.soilmate.common.enums.ErrorCode;
import com.soilmate.common.enums.UserRole;
import com.soilmate.common.exception.UserException;
import com.soilmate.common.security.dto.TokenResponse;
import com.soilmate.common.security.util.JwtUtil;
import com.soilmate.webservice.dto.request.LoginRequest;
import com.soilmate.webservice.dto.request.RefreshTokenRequest;
import com.soilmate.webservice.dto.request.RegisterRequest;
import com.soilmate.webservice.dto.response.AuthResponse;
import com.soilmate.webservice.dto.response.UserResponse;
import com.soilmate.webservice.entity.User;
import com.soilmate.webservice.entity.UserAuth;
import com.soilmate.webservice.mapper.UserAuthMapper;
import com.soilmate.webservice.mapper.UserMapper;
import com.soilmate.webservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final UserAuthMapper userAuthMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());

        // Check if email already exists
        if (isEmailExists(request.getEmail())) {
            log.warn("Registration failed: email already exists - {}", request.getEmail());
            throw new UserException(ErrorCode.USER_EMAIL_EXISTS);
        }

        // Create user entity
        User user = User.builder()
                .email(request.getEmail())
                .displayName(request.getDisplayName())
                .role(UserRole.USER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userMapper.insert(user);
        log.debug("Created user with id: {}", user.getId());

        // Create user auth entity with hashed password
        UserAuth userAuth = UserAuth.builder()
                .userId(user.getId())
                .authProvider(AuthProvider.EMAIL)
                .providerEmail(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .isPrimary(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userAuthMapper.insert(userAuth);
        log.debug("Created user auth for user id: {}", user.getId());

        // Generate tokens
        TokenResponse tokens = generateTokens(user);

        log.info("User registered successfully: {}", user.getId());

        return AuthResponse.builder()
                .user(UserResponse.fromEntity(user))
                .tokens(tokens)
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        return null;
    }

    @Override
    public void logout(Long userId) {
        // TODO: Implement token blacklisting if necessary
        log.info("User logout - client should discard tokens");
    }

    @Override
    public TokenResponse refreshToken(RefreshTokenRequest request) {
        return null;
    }

    /**
     * Check if email already exists in the system.
     */
    private boolean isEmailExists(String email) {
        return userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getEmail, email)
        ) > 0;
    }

    /**
     * Generate access and refresh tokens for a user.
     */
    private TokenResponse generateTokens(User user) {
        return jwtUtil.generateTokenPair(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.getRole()
        );
    }
}
