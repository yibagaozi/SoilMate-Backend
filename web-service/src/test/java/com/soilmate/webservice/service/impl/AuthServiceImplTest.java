package com.soilmate.webservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.soilmate.common.enums.AuthProvider;
import com.soilmate.common.enums.UserRole;
import com.soilmate.common.exception.AuthenticationException;
import com.soilmate.common.exception.UserException;
import com.soilmate.common.security.context.UserContext;
import com.soilmate.common.security.dto.TokenResponse;
import com.soilmate.common.security.util.JwtUtil;
import com.soilmate.webservice.dto.request.LoginRequest;
import com.soilmate.webservice.dto.request.RefreshTokenRequest;
import com.soilmate.webservice.dto.request.RegisterRequest;
import com.soilmate.webservice.dto.response.AuthResponse;
import com.soilmate.webservice.entity.User;
import com.soilmate.webservice.entity.UserAuth;
import com.soilmate.webservice.mapper.UserAuthMapper;
import com.soilmate.webservice.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserAuthMapper userAuthMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User testUser;
    private UserAuth testUserAuth;
    private TokenResponse testTokenResponse;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .email("test@example.com")
                .password("password123")
                .displayName("Test User")
                .build();

        loginRequest = LoginRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .displayName("Test User")
                .role(UserRole.USER)
                .build();

        testUserAuth = UserAuth.builder()
                .id(1L)
                .userId(1L)
                .authProvider(AuthProvider.EMAIL)
                .providerEmail("test@example.com")
                .passwordHash("hashedPassword")
                .isPrimary(true)
                .build();

        testTokenResponse = TokenResponse.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .build();
    }

    // ==================== register ====================

    @Test
    void register_success_returnsAuthResponse() {
        when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(userMapper.insert((User) any())).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return 1;
        });
        when(userAuthMapper.insert((UserAuth) any())).thenReturn(1);
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(jwtUtil.generateTokenPair(anyLong(), anyString(), anyString(), any(UserRole.class)))
                .thenReturn(testTokenResponse);

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertNotNull(response.getUser());
        assertEquals("test@example.com", response.getUser().getEmail());
        assertEquals("Test User", response.getUser().getDisplayName());
        assertNotNull(response.getTokens());
        assertEquals("access-token", response.getTokens().getAccessToken());

        verify(userMapper).insert((User) any());
        verify(userAuthMapper).insert((UserAuth) any());
        verify(passwordEncoder).encode("password123");
    }

    @Test
    void register_duplicateEmail_throwsUserException() {
        when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

        assertThrows(UserException.class, () -> authService.register(registerRequest));

        verify(userMapper, never()).insert((User) any());
        verify(userAuthMapper, never()).insert((UserAuth) any());
    }

    // ==================== login ====================

    @Test
    void login_success_returnsAuthResponse() {
        when(userAuthMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testUserAuth);
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(jwtUtil.generateTokenPair(anyLong(), anyString(), anyString(), any(UserRole.class)))
                .thenReturn(testTokenResponse);

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("test@example.com", response.getUser().getEmail());
        assertNotNull(response.getTokens());
    }

    @Test
    void login_userNotFound_throwsAuthenticationException() {
        when(userAuthMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        assertThrows(AuthenticationException.class, () -> authService.login(loginRequest));
    }

    @Test
    void login_wrongPassword_throwsAuthenticationException() {
        when(userAuthMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testUserAuth);
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(false);

        assertThrows(AuthenticationException.class, () -> authService.login(loginRequest));

        verify(userMapper, never()).selectById(anyLong());
    }

    @Test
    void login_userEntityMissing_throwsUserException() {
        when(userAuthMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testUserAuth);
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);
        when(userMapper.selectById(1L)).thenReturn(null);

        assertThrows(UserException.class, () -> authService.login(loginRequest));
    }

    // ==================== refreshToken ====================

    @Test
    void refreshToken_success_returnsNewTokens() {
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken("old-refresh-token")
                .build();

        UserContext userContext = new UserContext();
        userContext.setUserId(1L);

        when(jwtUtil.validateRefreshToken("old-refresh-token")).thenReturn(userContext);
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(jwtUtil.generateTokenPair(anyLong(), anyString(), anyString(), any(UserRole.class)))
                .thenReturn(testTokenResponse);

        TokenResponse response = authService.refreshToken(request);

        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
    }

    @Test
    void refreshToken_userNotFound_throwsUserException() {
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken("old-refresh-token")
                .build();

        UserContext userContext = new UserContext();
        userContext.setUserId(999L);

        when(jwtUtil.validateRefreshToken("old-refresh-token")).thenReturn(userContext);
        when(userMapper.selectById(999L)).thenReturn(null);

        assertThrows(UserException.class, () -> authService.refreshToken(request));
    }
}