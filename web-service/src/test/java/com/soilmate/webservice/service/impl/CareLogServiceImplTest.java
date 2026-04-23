package com.soilmate.webservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.soilmate.common.enums.CareType;
import com.soilmate.common.exception.ResourceNotFoundException;
import com.soilmate.webservice.dto.request.AddCareLogRequest;
import com.soilmate.webservice.dto.response.CareLogItemResponse;
import com.soilmate.webservice.entity.CareLog;
import com.soilmate.webservice.entity.Plant;
import com.soilmate.webservice.entity.UserPlant;
import com.soilmate.webservice.mapper.CareLogMapper;
import com.soilmate.webservice.mapper.PlantMapper;
import com.soilmate.webservice.mapper.UserPlantMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CareLogServiceImplTest {

    @Mock
    private CareLogMapper careLogMapper;

    @Mock
    private UserPlantMapper userPlantMapper;

    @Mock
    private PlantMapper plantMapper;

    @InjectMocks
    private CareLogServiceImpl careLogService;

    private UserPlant testUserPlant;
    private Plant testPlant;
    private CareLog testCareLog;

    @BeforeEach
    void setUp() {
        testPlant = Plant.builder()
                .id(1L)
                .name("Monstera")
                .imageUrl("https://example.com/monstera.jpg")
                .build();

        testUserPlant = UserPlant.builder()
                .id(10L)
                .userId(1L)
                .plantId(1L)
                .nickname("My Monstera")
                .location("Living room")
                .active(true)
                .build();

        testCareLog = CareLog.builder()
                .id(100L)
                .userPlantId(10L)
                .careType(CareType.WATER)
                .performedAt(LocalDateTime.now())
                .waterAmountMl(300)
                .notes("Gave extra water")
                .build();
    }

    // ==================== getLogsByDate ====================

    @Test
    void getLogsByDate_withLogs_returnsList() {
        when(userPlantMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(testUserPlant));
        when(careLogMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(testCareLog));
        when(plantMapper.selectBatchIds(anyList())).thenReturn(List.of(testPlant));

        List<CareLogItemResponse> result = careLogService.getLogsByDate(1L, LocalDate.now(), null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(CareType.WATER, result.get(0).getCareType());
        assertEquals("My Monstera", result.get(0).getPlantNickname());
        assertEquals("Living room", result.get(0).getPlantLocation());
    }

    @Test
    void getLogsByDate_noPlants_returnsEmptyList() {
        when(userPlantMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        List<CareLogItemResponse> result = careLogService.getLogsByDate(1L, LocalDate.now(), null);

        assertTrue(result.isEmpty());
        verify(careLogMapper, never()).selectList(any());
    }

    @Test
    void getLogsByDate_filterByUserPlantId_queriesSpecificPlant() {
        when(userPlantMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(testUserPlant));
        when(careLogMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        List<CareLogItemResponse> result = careLogService.getLogsByDate(1L, LocalDate.now(), 10L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ==================== getRecentLogs ====================

    @Test
    void getRecentLogs_withLogs_returnsList() {
        when(careLogMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(testCareLog));
        when(userPlantMapper.selectById(10L)).thenReturn(testUserPlant);
        when(plantMapper.selectBatchIds(anyList())).thenReturn(List.of(testPlant));

        List<CareLogItemResponse> result = careLogService.getRecentLogs(10L, 3);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getRecentLogs_noLogs_returnsEmptyList() {
        when(careLogMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        List<CareLogItemResponse> result = careLogService.getRecentLogs(10L, 3);

        assertTrue(result.isEmpty());
        verify(userPlantMapper, never()).selectById(anyLong());
    }

    // ==================== addCareLog ====================

    @Test
    void addCareLog_wateringSuccess_returnsResponse() {
        AddCareLogRequest request = new AddCareLogRequest();
        request.setUserPlantId(10L);
        request.setCareType(CareType.WATER);
        request.setWaterAmountMl(300);
        request.setNotes("Morning watering");

        when(userPlantMapper.selectById(10L)).thenReturn(testUserPlant);
        when(careLogMapper.insert((CareLog) any())).thenAnswer(invocation -> {
            CareLog log = invocation.getArgument(0);
            log.setId(101L);
            return 1;
        });
        when(userPlantMapper.updateById((UserPlant) any())).thenReturn(1);
        when(plantMapper.selectById(1L)).thenReturn(testPlant);

        CareLogItemResponse response = careLogService.addCareLog(1L, request);

        assertNotNull(response);
        assertEquals(101L, response.getId());
        assertEquals(CareType.WATER, response.getCareType());
        assertEquals(300, response.getWaterAmountMl());
        assertEquals("Morning watering", response.getNotes());
        assertEquals("My Monstera", response.getPlantNickname());

        ArgumentCaptor<UserPlant> captor = ArgumentCaptor.forClass(UserPlant.class);
        verify(userPlantMapper).updateById(captor.capture());
        assertNotNull(captor.getValue().getLastWateredAt());
    }

    @Test
    void addCareLog_feedingSuccess_updatesLastFedAt() {
        AddCareLogRequest request = new AddCareLogRequest();
        request.setUserPlantId(10L);
        request.setCareType(CareType.FEED);
        request.setFeedingType("liquid fertilizer");

        when(userPlantMapper.selectById(10L)).thenReturn(testUserPlant);
        when(careLogMapper.insert((CareLog) any())).thenAnswer(invocation -> {
            CareLog log = invocation.getArgument(0);
            log.setId(102L);
            return 1;
        });
        when(userPlantMapper.updateById((UserPlant) any())).thenReturn(1);
        when(plantMapper.selectById(1L)).thenReturn(testPlant);

        CareLogItemResponse response = careLogService.addCareLog(1L, request);

        assertNotNull(response);
        assertEquals(CareType.FEED, response.getCareType());

        ArgumentCaptor<UserPlant> captor = ArgumentCaptor.forClass(UserPlant.class);
        verify(userPlantMapper).updateById(captor.capture());
        assertNotNull(captor.getValue().getLastFedAt());
    }

    @Test
    void addCareLog_plantNotOwned_throwsResourceNotFoundException() {
        AddCareLogRequest request = new AddCareLogRequest();
        request.setUserPlantId(10L);

        UserPlant otherUserPlant = UserPlant.builder()
                .id(10L)
                .userId(999L)
                .active(true)
                .build();
        when(userPlantMapper.selectById(10L)).thenReturn(otherUserPlant);

        assertThrows(ResourceNotFoundException.class,
                () -> careLogService.addCareLog(1L, request));

        verify(careLogMapper, never()).insert((CareLog) any());
    }

    @Test
    void addCareLog_inactivePlant_throwsResourceNotFoundException() {
        AddCareLogRequest request = new AddCareLogRequest();
        request.setUserPlantId(10L);

        UserPlant inactivePlant = UserPlant.builder()
                .id(10L)
                .userId(1L)
                .active(false)
                .build();
        when(userPlantMapper.selectById(10L)).thenReturn(inactivePlant);

        assertThrows(ResourceNotFoundException.class,
                () -> careLogService.addCareLog(1L, request));
    }

    @Test
    void addCareLog_plantNotFound_throwsResourceNotFoundException() {
        AddCareLogRequest request = new AddCareLogRequest();
        request.setUserPlantId(999L);

        when(userPlantMapper.selectById(999L)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class,
                () -> careLogService.addCareLog(1L, request));
    }

    @Test
    void addCareLog_withCustomPerformedAt_usesProvidedTime() {
        LocalDateTime customTime = LocalDateTime.of(2025, 6, 15, 8, 30);

        AddCareLogRequest request = new AddCareLogRequest();
        request.setUserPlantId(10L);
        request.setCareType(CareType.WATER);
        request.setPerformedAt(customTime);

        when(userPlantMapper.selectById(10L)).thenReturn(testUserPlant);
        when(careLogMapper.insert((CareLog) any())).thenAnswer(invocation -> {
            CareLog log = invocation.getArgument(0);
            log.setId(103L);
            assertEquals(customTime, log.getPerformedAt());
            return 1;
        });
        when(userPlantMapper.updateById((UserPlant) any())).thenReturn(1);
        when(plantMapper.selectById(1L)).thenReturn(testPlant);

        CareLogItemResponse response = careLogService.addCareLog(1L, request);

        assertNotNull(response);
        assertEquals(customTime, response.getPerformedAt());
    }

    // ==================== deleteLogsByUserPlantId ====================

    @Test
    void deleteLogsByUserPlantId_success_deletesAll() {
        when(careLogMapper.delete(any())).thenReturn(5);

        careLogService.deleteLogsByUserPlantId(10L);

        verify(careLogMapper).delete(any());
    }
}