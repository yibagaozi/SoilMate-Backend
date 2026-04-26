package com.soilmate.webservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.soilmate.common.enums.AuthProvider;
import com.soilmate.common.enums.Region;
import com.soilmate.common.enums.UserRole;
import com.soilmate.common.exception.UserException;
import com.soilmate.common.security.context.UserContextHolder;
import com.soilmate.webservice.dto.request.UpdateUserRequest;
import com.soilmate.webservice.dto.response.UserProfileResponse;
import com.soilmate.webservice.entity.User;
import com.soilmate.webservice.entity.UserAuth;
import com.soilmate.webservice.mapper.UserAuthMapper;
import com.soilmate.webservice.mapper.UserMapper;
import com.soilmate.webservice.mapper.UserPlantMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserAuthMapper userAuthMapper;

    @Mock
    private UserPlantMapper userPlantMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private MockedStatic<UserContextHolder> userContextHolderMock;
    private User testUser;
    private UserAuth testUserAuth;

    @BeforeEach
    void setUp() {
        userContextHolderMock = mockStatic(UserContextHolder.class);
        userContextHolderMock.when(UserContextHolder::getCurrentUserId).thenReturn(1L);

        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .displayName("Test User")
                .timezone("UTC")
                .notificationEnabled(true)
                .reminderTime(LocalTime.of(9, 0))
                .role(UserRole.USER)
                .region(Region.BEIJING)
                .build();

        testUserAuth = UserAuth.builder()
                .id(1L)
                .userId(1L)
                .authProvider(AuthProvider.EMAIL)
                .providerEmail("test@example.com")
                .isPrimary(true)
                .build();
    }

    @AfterEach
    void tearDown() {
        userContextHolderMock.close();
    }

    // ==================== getMyProfile ====================

    @Test
    void getMyProfile_success_returnsFullProfile() {
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(userAuthMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(testUserAuth));
        when(userPlantMapper.countTotalPlants(1L)).thenReturn(5);
        when(userPlantMapper.countActivePlants(1L)).thenReturn(3);

        UserProfileResponse response = userService.getMyProfile();

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("test@example.com", response.getEmail());
        assertEquals("Test User", response.getDisplayName());
        assertEquals("UTC", response.getTimezone());
        assertTrue(response.getNotificationEnabled());
        assertEquals(LocalTime.of(9, 0), response.getReminderTime());
        assertEquals(Region.BEIJING, response.getRegion());

        // Auth methods
        assertNotNull(response.getAuthMethods());
        assertEquals(1, response.getAuthMethods().size());
        assertEquals(AuthProvider.EMAIL.getCode(), response.getAuthMethods().get(0).getProvider());
        assertTrue(response.getAuthMethods().get(0).getIsPrimary());

        // Stats
        assertNotNull(response.getStats());
        assertEquals(5, response.getStats().getTotalPlants());
        assertEquals(3, response.getStats().getActivePlants());
    }

    @Test
    void getMyProfile_userNotFound_throwsUserException() {
        when(userMapper.selectById(1L)).thenReturn(null);

        assertThrows(UserException.class, () -> userService.getMyProfile());
    }

    // ==================== updateMyProfile ====================

    @Test
    void updateMyProfile_updateDisplayName_success() {
        UpdateUserRequest request = UpdateUserRequest.builder()
                .displayName("New Name")
                .build();

        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(userMapper.updateById((User) any())).thenReturn(1);
        when(userAuthMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(testUserAuth));
        when(userPlantMapper.countTotalPlants(1L)).thenReturn(5);
        when(userPlantMapper.countActivePlants(1L)).thenReturn(3);

        UserProfileResponse response = userService.updateMyProfile(request);

        assertNotNull(response);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).updateById(captor.capture());
        assertEquals("New Name", captor.getValue().getDisplayName());
    }

    @Test
    void updateMyProfile_updateTimezone_success() {
        UpdateUserRequest request = UpdateUserRequest.builder()
                .timezone("Asia/Shanghai")
                .build();

        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(userMapper.updateById((User) any())).thenReturn(1);
        when(userAuthMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(testUserAuth));
        when(userPlantMapper.countTotalPlants(1L)).thenReturn(0);
        when(userPlantMapper.countActivePlants(1L)).thenReturn(0);

        userService.updateMyProfile(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).updateById(captor.capture());
        assertEquals("Asia/Shanghai", captor.getValue().getTimezone());
    }

    @Test
    void updateMyProfile_updateNotificationSettings_success() {
        UpdateUserRequest request = UpdateUserRequest.builder()
                .notificationEnabled(false)
                .reminderTime(LocalTime.of(20, 0))
                .build();

        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(userMapper.updateById((User) any())).thenReturn(1);
        when(userAuthMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(testUserAuth));
        when(userPlantMapper.countTotalPlants(1L)).thenReturn(0);
        when(userPlantMapper.countActivePlants(1L)).thenReturn(0);

        userService.updateMyProfile(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).updateById(captor.capture());

        User updated = captor.getValue();
        assertFalse(updated.getNotificationEnabled());
        assertEquals(LocalTime.of(20, 0), updated.getReminderTime());
    }

    @Test
    void updateMyProfile_updateRegion_success() {
        UpdateUserRequest request = UpdateUserRequest.builder()
                .region(Region.BEIJING)
                .build();

        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(userMapper.updateById((User) any())).thenReturn(1);
        when(userAuthMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(testUserAuth));
        when(userPlantMapper.countTotalPlants(1L)).thenReturn(0);
        when(userPlantMapper.countActivePlants(1L)).thenReturn(0);

        userService.updateMyProfile(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).updateById(captor.capture());
        assertEquals(Region.BEIJING, captor.getValue().getRegion());
    }

    @Test
    void updateMyProfile_nullFields_noChanges() {
        UpdateUserRequest request = UpdateUserRequest.builder().build();

        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(userMapper.updateById((User) any())).thenReturn(1);
        when(userAuthMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(testUserAuth));
        when(userPlantMapper.countTotalPlants(1L)).thenReturn(0);
        when(userPlantMapper.countActivePlants(1L)).thenReturn(0);

        userService.updateMyProfile(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).updateById(captor.capture());

        User updated = captor.getValue();
        assertEquals("Test User", updated.getDisplayName());
        assertEquals("UTC", updated.getTimezone());
        assertTrue(updated.getNotificationEnabled());
    }

    @Test
    void updateMyProfile_userNotFound_throwsUserException() {
        UpdateUserRequest request = UpdateUserRequest.builder()
                .displayName("New Name")
                .build();

        when(userMapper.selectById(1L)).thenReturn(null);

        assertThrows(UserException.class, () -> userService.updateMyProfile(request));

        verify(userMapper, never()).updateById((User) any());
    }
}