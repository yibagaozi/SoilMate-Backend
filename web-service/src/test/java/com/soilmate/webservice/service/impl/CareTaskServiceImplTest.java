package com.soilmate.webservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.soilmate.common.enums.CareType;
import com.soilmate.common.enums.TaskStatus;
import com.soilmate.common.exception.BusinessException;
import com.soilmate.common.exception.ResourceNotFoundException;
import com.soilmate.webservice.dto.request.CompleteTaskRequest;
import com.soilmate.webservice.dto.request.SkipTaskRequest;
import com.soilmate.webservice.dto.response.CareTaskItemResponse;
import com.soilmate.webservice.dto.response.TaskDetailResponse;
import com.soilmate.webservice.entity.CareLog;
import com.soilmate.webservice.entity.CareTask;
import com.soilmate.webservice.entity.Plant;
import com.soilmate.webservice.entity.UserPlant;
import com.soilmate.webservice.mapper.CareLogMapper;
import com.soilmate.webservice.mapper.CareTaskMapper;
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
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CareTaskServiceImplTest {

    @Mock
    private CareTaskMapper careTaskMapper;

    @Mock
    private CareLogMapper careLogMapper;

    @Mock
    private UserPlantMapper userPlantMapper;

    @Mock
    private PlantMapper plantMapper;

    @InjectMocks
    private CareTaskServiceImpl careTaskService;

    private UserPlant testUserPlant;
    private Plant testPlant;
    private CareTask testWaterTask;

    @BeforeEach
    void setUp() {
        testPlant = Plant.builder()
                .id(1L)
                .name("Monstera")
                .wateringIntervalDays(7)
                .wateringAmountMl(300)
                .feedingIntervalDays(30)
                .feedingType("liquid fertilizer")
                .build();

        testUserPlant = UserPlant.builder()
                .id(10L)
                .userId(1L)
                .plantId(1L)
                .nickname("My Monstera")
                .active(true)
                .customWateringIntervalDays(7)
                .customWateringAmountMl(300)
                .customFeedingIntervalDays(30)
                .customFeedingType("liquid fertilizer")
                .build();

        testWaterTask = CareTask.builder()
                .id(100L)
                .userPlantId(10L)
                .careType(CareType.WATER)
                .scheduledDate(LocalDate.now())
                .waterAmountMl(300)
                .status(TaskStatus.PENDING)
                .build();
    }

    // ==================== getTodayTasks ====================

    @Test
    void getTodayTasks_withTasks_returnsList() {
        when(userPlantMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(testUserPlant));
        when(careTaskMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(testWaterTask));
        when(plantMapper.selectBatchIds(anyList())).thenReturn(List.of(testPlant));

        List<CareTaskItemResponse> result = careTaskService.getTodayTasks(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(CareType.WATER, result.get(0).getCareType());
        assertEquals("My Monstera", result.get(0).getPlantNickname());
    }

    @Test
    void getTodayTasks_noPlants_returnsEmptyList() {
        when(userPlantMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        List<CareTaskItemResponse> result = careTaskService.getTodayTasks(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void getTodayTasks_noTasks_returnsEmptyList() {
        when(userPlantMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(testUserPlant));
        when(careTaskMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        List<CareTaskItemResponse> result = careTaskService.getTodayTasks(1L);

        assertTrue(result.isEmpty());
    }

    // ==================== completeTask ====================

    @Test
    void completeTask_success_returnsTaskDetail() {
        CompleteTaskRequest request = new CompleteTaskRequest();
        request.setNotes("Done watering");

        when(careTaskMapper.selectById(100L)).thenReturn(testWaterTask);
        when(userPlantMapper.selectById(10L)).thenReturn(testUserPlant);
        when(careTaskMapper.updateById((CareTask) any())).thenReturn(1);
        when(careLogMapper.insert((CareLog) any())).thenReturn(1);
        when(userPlantMapper.updateById((UserPlant) any())).thenReturn(1);
        when(plantMapper.selectById(1L)).thenReturn(testPlant);
        when(careTaskMapper.insert((CareTask) any())).thenAnswer(invocation -> {
            CareTask task = invocation.getArgument(0);
            task.setId(101L);
            return 1;
        });

        TaskDetailResponse response = careTaskService.completeTask(1L, 100L, request);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals(TaskStatus.COMPLETED, response.getStatus());
        assertNotNull(response.getCompletedAt());
        assertEquals(101L, response.getNextTaskId());
        assertNull(response.getSkipReason());

        verify(careLogMapper).insert((CareLog) any());
        verify(careTaskMapper, times(1)).insert((CareTask) any());
    }

    @Test
    void completeTask_withCustomWaterAmount_usesRequestValue() {
        CompleteTaskRequest request = new CompleteTaskRequest();
        request.setWaterAmountMl(500);

        when(careTaskMapper.selectById(100L)).thenReturn(testWaterTask);
        when(userPlantMapper.selectById(10L)).thenReturn(testUserPlant);
        when(careTaskMapper.updateById((CareTask) any())).thenReturn(1);
        when(careLogMapper.insert((CareLog) any())).thenReturn(1);
        when(userPlantMapper.updateById((UserPlant) any())).thenReturn(1);
        when(plantMapper.selectById(1L)).thenReturn(testPlant);
        when(careTaskMapper.insert((CareTask) any())).thenAnswer(invocation -> {
            CareTask task = invocation.getArgument(0);
            task.setId(101L);
            return 1;
        });

        TaskDetailResponse response = careTaskService.completeTask(1L, 100L, request);

        assertNotNull(response);
        assertEquals(TaskStatus.COMPLETED, response.getStatus());

        ArgumentCaptor<CareLog> logCaptor = ArgumentCaptor.forClass(CareLog.class);
        verify(careLogMapper).insert(logCaptor.capture());
        assertEquals(500, logCaptor.getValue().getWaterAmountMl());
    }

    @Test
    void completeTask_taskNotFound_throwsResourceNotFoundException() {
        when(careTaskMapper.selectById(999L)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class,
                () -> careTaskService.completeTask(1L, 999L, new CompleteTaskRequest()));
    }

    @Test
    void completeTask_notOwner_throwsResourceNotFoundException() {
        when(careTaskMapper.selectById(100L)).thenReturn(testWaterTask);

        UserPlant otherUserPlant = UserPlant.builder()
                .id(10L)
                .userId(999L)
                .build();
        when(userPlantMapper.selectById(10L)).thenReturn(otherUserPlant);

        assertThrows(ResourceNotFoundException.class,
                () -> careTaskService.completeTask(1L, 100L, new CompleteTaskRequest()));
    }

    @Test
    void completeTask_alreadyCompleted_throwsBusinessException() {
        CareTask completedTask = CareTask.builder()
                .id(100L)
                .userPlantId(10L)
                .status(TaskStatus.COMPLETED)
                .build();

        when(careTaskMapper.selectById(100L)).thenReturn(completedTask);
        when(userPlantMapper.selectById(10L)).thenReturn(testUserPlant);

        assertThrows(BusinessException.class,
                () -> careTaskService.completeTask(1L, 100L, new CompleteTaskRequest()));
    }

    // ==================== skipTask ====================

    @Test
    void skipTask_success_returnsTaskDetail() {
        SkipTaskRequest request = new SkipTaskRequest();
        request.setReason("On vacation");

        when(careTaskMapper.selectById(100L)).thenReturn(testWaterTask);
        when(userPlantMapper.selectById(10L)).thenReturn(testUserPlant);
        when(careTaskMapper.updateById((CareTask) any())).thenReturn(1);
        when(plantMapper.selectById(1L)).thenReturn(testPlant);
        when(careTaskMapper.insert((CareTask) any())).thenAnswer(invocation -> {
            CareTask task = invocation.getArgument(0);
            task.setId(101L);
            return 1;
        });

        TaskDetailResponse response = careTaskService.skipTask(1L, 100L, request);

        assertNotNull(response);
        assertEquals(TaskStatus.SKIPPED, response.getStatus());
        assertEquals("On vacation", response.getSkipReason());
        assertEquals(101L, response.getNextTaskId());

        verify(careLogMapper, never()).insert((CareLog) any());
    }

    @Test
    void skipTask_alreadyProcessed_throwsBusinessException() {
        CareTask skippedTask = CareTask.builder()
                .id(100L)
                .userPlantId(10L)
                .status(TaskStatus.SKIPPED)
                .build();

        when(careTaskMapper.selectById(100L)).thenReturn(skippedTask);
        when(userPlantMapper.selectById(10L)).thenReturn(testUserPlant);

        assertThrows(BusinessException.class,
                () -> careTaskService.skipTask(1L, 100L, new SkipTaskRequest()));
    }

    // ==================== createInitialTasks ====================

    @Test
    void createInitialTasks_success_createsTwoTasks() {
        when(careTaskMapper.insert((CareTask) any())).thenReturn(1);

        careTaskService.createInitialTasks(10L, 7, 300, 30, "liquid fertilizer");

        ArgumentCaptor<CareTask> captor = ArgumentCaptor.forClass(CareTask.class);
        verify(careTaskMapper, times(2)).insert(captor.capture());

        List<CareTask> insertedTasks = captor.getAllValues();

        CareTask waterTask = insertedTasks.get(0);
        assertEquals(10L, waterTask.getUserPlantId());
        assertEquals(CareType.WATER, waterTask.getCareType());
        assertEquals(LocalDate.now().plusDays(7), waterTask.getScheduledDate());
        assertEquals(300, waterTask.getWaterAmountMl());
        assertEquals(TaskStatus.PENDING, waterTask.getStatus());

        CareTask feedTask = insertedTasks.get(1);
        assertEquals(10L, feedTask.getUserPlantId());
        assertEquals(CareType.FEED, feedTask.getCareType());
        assertEquals(LocalDate.now().plusDays(30), feedTask.getScheduledDate());
        assertEquals("liquid fertilizer", feedTask.getFeedingType());
        assertEquals(TaskStatus.PENDING, feedTask.getStatus());
    }

    // ==================== deleteTasksByUserPlantId ====================

    @Test
    void deleteTasksByUserPlantId_success_deletesAll() {
        when(careTaskMapper.delete(any())).thenReturn(3);

        careTaskService.deleteTasksByUserPlantId(10L);

        verify(careTaskMapper).delete(any());
    }
}