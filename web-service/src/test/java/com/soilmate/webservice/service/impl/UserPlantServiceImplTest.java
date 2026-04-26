package com.soilmate.webservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.soilmate.common.enums.CareType;
import com.soilmate.common.enums.GrowingEnvironment;
import com.soilmate.common.enums.PotSize;
import com.soilmate.common.enums.SunlightRequirement;
import com.soilmate.common.exception.ResourceNotFoundException;
import com.soilmate.webservice.dto.request.AddUserPlantRequest;
import com.soilmate.webservice.dto.request.UpdateUserPlantRequest;
import com.soilmate.webservice.dto.response.CareLogItemResponse;
import com.soilmate.webservice.dto.response.UserPlantDetailResponse;
import com.soilmate.webservice.entity.Plant;
import com.soilmate.webservice.entity.UserPlant;
import com.soilmate.webservice.mapper.BarcodeMapper;
import com.soilmate.webservice.mapper.CareTaskMapper;
import com.soilmate.webservice.mapper.PlantMapper;
import com.soilmate.webservice.mapper.UserPlantMapper;
import com.soilmate.webservice.service.CareLogService;
import com.soilmate.webservice.service.CareTaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserPlantServiceImplTest {

    @Mock
    private UserPlantMapper userPlantMapper;

    @Mock
    private PlantMapper plantMapper;

    @Mock
    private BarcodeMapper barcodeMapper;

    @Mock
    private CareTaskMapper careTaskMapper;

    @Mock
    private CareTaskService careTaskService;

    @Mock
    private CareLogService careLogService;

    @InjectMocks
    private UserPlantServiceImpl userPlantService;

    private Plant testPlant;
    private UserPlant testUserPlant;

    @BeforeEach
    void setUp() {
        testPlant = Plant.builder()
                .id(1L)
                .name("Monstera")
                .scientificName("Monstera deliciosa")
                .wateringIntervalDays(7)
                .wateringAmountMl(300)
                .feedingIntervalDays(30)
                .feedingType("liquid fertilizer")
                .sunlightRequirement(SunlightRequirement.LOW)
                .imageUrl("https://example.com/monstera.jpg")
                .build();

        testUserPlant = UserPlant.builder()
                .id(10L)
                .userId(1L)
                .plantId(1L)
                .nickname("My Monstera")
                .location("Living room")
                .active(true)
                .customWateringIntervalDays(7)
                .customWateringAmountMl(300)
                .customFeedingIntervalDays(30)
                .customFeedingType("liquid fertilizer")
                .build();
    }

    // ==================== addUserPlant ====================

    @Test
    void addUserPlant_success_returnsId() {
        AddUserPlantRequest request = AddUserPlantRequest.builder()
                .plantId(1L)
                .nickname("My Monstera")
                .location("Living room")
                .build();

        when(plantMapper.selectById(1L)).thenReturn(testPlant);
        when(userPlantMapper.insert((UserPlant) any())).thenAnswer(invocation -> {
            UserPlant up = invocation.getArgument(0);
            up.setId(10L);
            return 1;
        });
        doNothing().when(careTaskService)
                .createInitialTasks(anyLong(), anyInt(), anyInt(), anyInt(), anyString());

        Long id = userPlantService.addUserPlant(1L, request);

        assertEquals(10L, id);
        verify(userPlantMapper).insert((UserPlant) any());
        verify(careTaskService).createInitialTasks(eq(10L), eq(7), eq(300), eq(30), eq("liquid fertilizer"));
    }

    @Test
    void addUserPlant_withCustomIntervals_usesCustomValues() {
        AddUserPlantRequest request = AddUserPlantRequest.builder()
                .plantId(1L)
                .nickname("Custom Monstera")
                .wateringIntervalDays(5)
                .wateringAmountMl(200)
                .feedingIntervalDays(14)
                .feedingType("granular")
                .potSize(PotSize.MEDIUM)
                .environment(GrowingEnvironment.INDOOR)
                .build();

        when(plantMapper.selectById(1L)).thenReturn(testPlant);
        when(userPlantMapper.insert((UserPlant) any())).thenAnswer(invocation -> {
            UserPlant up = invocation.getArgument(0);
            up.setId(11L);
            return 1;
        });
        doNothing().when(careTaskService)
                .createInitialTasks(anyLong(), anyInt(), anyInt(), anyInt(), anyString());

        Long id = userPlantService.addUserPlant(1L, request);

        assertEquals(11L, id);
        verify(careTaskService).createInitialTasks(eq(11L), eq(5), eq(200), eq(14), eq("granular"));
    }

    @Test
    void addUserPlant_plantNotFound_throwsResourceNotFoundException() {
        AddUserPlantRequest request = AddUserPlantRequest.builder()
                .plantId(999L)
                .build();

        when(plantMapper.selectById(999L)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class,
                () -> userPlantService.addUserPlant(1L, request));

        verify(userPlantMapper, never()).insert((UserPlant) any());
        verify(careTaskService, never()).createInitialTasks(anyLong(), anyInt(), anyInt(), anyInt(), anyString());
    }

    // ==================== updateUserPlant ====================

    @Test
    void updateUserPlant_success_updatesFields() {
        UpdateUserPlantRequest request = UpdateUserPlantRequest.builder()
                .nickname("Renamed Monstera")
                .location("Bedroom")
                .wateringIntervalDays(5)
                .build();

        when(userPlantMapper.selectById(10L)).thenReturn(testUserPlant);
        when(userPlantMapper.updateById((UserPlant) any())).thenReturn(1);

        userPlantService.updateUserPlant(1L, 10L, request);

        ArgumentCaptor<UserPlant> captor = ArgumentCaptor.forClass(UserPlant.class);
        verify(userPlantMapper).updateById(captor.capture());

        UserPlant updated = captor.getValue();
        assertEquals("Renamed Monstera", updated.getNickname());
        assertEquals("Bedroom", updated.getLocation());
        assertEquals(5, updated.getCustomWateringIntervalDays());
    }

    @Test
    void updateUserPlant_notOwned_throwsResourceNotFoundException() {
        UpdateUserPlantRequest request = UpdateUserPlantRequest.builder()
                .nickname("Hacked")
                .build();

        when(userPlantMapper.selectById(10L)).thenReturn(testUserPlant);

        assertThrows(ResourceNotFoundException.class,
                () -> userPlantService.updateUserPlant(999L, 10L, request));
    }

    @Test
    void updateUserPlant_plantNotFound_throwsResourceNotFoundException() {
        when(userPlantMapper.selectById(999L)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class,
                () -> userPlantService.updateUserPlant(1L, 999L, new UpdateUserPlantRequest()));
    }

    // ==================== getUserPlants ====================

    @Test
    void getUserPlants_withPlants_returnsList() {
        when(userPlantMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(testUserPlant));
        when(plantMapper.selectBatchIds(anyList())).thenReturn(List.of(testPlant));
        when(careTaskMapper.selectNextTaskDatesRaw(anyList(), eq(CareType.WATER.name())))
                .thenReturn(new HashMap<>());
        when(careTaskMapper.selectNextTaskDatesRaw(anyList(), eq(CareType.FEED.name())))
                .thenReturn(new HashMap<>());

        List<UserPlantDetailResponse> result = userPlantService.getUserPlants(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("My Monstera", result.get(0).getNickname());
        assertEquals("Monstera", result.get(0).getPlantName());
        assertEquals("Living room", result.get(0).getLocation());
    }

    @Test
    void getUserPlants_noPlants_returnsEmptyList() {
        when(userPlantMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        List<UserPlantDetailResponse> result = userPlantService.getUserPlants(1L);

        assertTrue(result.isEmpty());
    }

    // ==================== getUserPlantDetail ====================

    @Test
    void getUserPlantDetail_success_returnsFullDetail() {
        when(userPlantMapper.selectById(10L)).thenReturn(testUserPlant);
        when(plantMapper.selectById(1L)).thenReturn(testPlant);
        when(careTaskMapper.selectNextTaskDate(10L, CareType.WATER.name()))
                .thenReturn(LocalDate.now().plusDays(3));
        when(careTaskMapper.selectNextTaskDate(10L, CareType.FEED.name()))
                .thenReturn(LocalDate.now().plusDays(15));
        when(careLogService.getRecentLogs(10L, 3))
                .thenReturn(Collections.emptyList());

        UserPlantDetailResponse response = userPlantService.getUserPlantDetail(1L, 10L);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("My Monstera", response.getNickname());
        assertEquals("Monstera", response.getPlantName());
        assertEquals("Monstera deliciosa", response.getScientificName());
        assertNotNull(response.getNextWateringDate());
        assertNotNull(response.getNextFeedingDate());
    }

    @Test
    void getUserPlantDetail_notOwned_throwsResourceNotFoundException() {
        when(userPlantMapper.selectById(10L)).thenReturn(testUserPlant);

        assertThrows(ResourceNotFoundException.class,
                () -> userPlantService.getUserPlantDetail(999L, 10L));
    }

    // ==================== deleteUserPlant ====================

    @Test
    void deleteUserPlant_success_softDeletesAndCleansUp() {
        when(userPlantMapper.selectById(10L)).thenReturn(testUserPlant);
        when(userPlantMapper.updateById((UserPlant) any())).thenReturn(1);
        doNothing().when(careTaskService).deleteTasksByUserPlantId(10L);
        doNothing().when(careLogService).deleteLogsByUserPlantId(10L);

        userPlantService.deleteUserPlant(1L, 10L);

        ArgumentCaptor<UserPlant> captor = ArgumentCaptor.forClass(UserPlant.class);
        verify(userPlantMapper).updateById(captor.capture());
        assertFalse(captor.getValue().getActive());

        verify(careTaskService).deleteTasksByUserPlantId(10L);
        verify(careLogService).deleteLogsByUserPlantId(10L);
    }

    @Test
    void deleteUserPlant_notOwned_throwsResourceNotFoundException() {
        when(userPlantMapper.selectById(10L)).thenReturn(testUserPlant);

        assertThrows(ResourceNotFoundException.class,
                () -> userPlantService.deleteUserPlant(999L, 10L));

        verify(userPlantMapper, never()).updateById((UserPlant) any());
        verify(careTaskService, never()).deleteTasksByUserPlantId(anyLong());
    }
}