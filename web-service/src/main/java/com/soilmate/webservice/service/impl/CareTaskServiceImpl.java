package com.soilmate.webservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.soilmate.common.enums.CareType;
import com.soilmate.common.enums.ErrorCode;
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
import com.soilmate.webservice.service.CareTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CareTaskServiceImpl implements CareTaskService {

    private final CareTaskMapper careTaskMapper;
    private final CareLogMapper careLogMapper;
    private final UserPlantMapper userPlantMapper;
    private final PlantMapper plantMapper;

    @Override
    public List<CareTaskItemResponse> getTodayTasks(Long userId) {
        LocalDate today = LocalDate.now();
        return getTasksForDateRange(userId, null, today);
    }

    @Override
    public List<CareTaskItemResponse> getUpcomingTasks(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(3);
        return getTasksForDateRange(userId, today, endDate);
    }

    @Override
    public List<CareTaskItemResponse> getTasksByDate(Long userId, LocalDate date) {
        return getTasksForDateRange(userId, date, date);
    }

    @Override
    @Transactional
    public TaskDetailResponse completeTask(Long userId, Long taskId, CompleteTaskRequest request) {
        CareTask task = getTaskOrThrow(userId, taskId);

        if (task.getStatus() != TaskStatus.PENDING) {
            throw new BusinessException(ErrorCode.TASK_ALREADY_PROCESSED);
        }

        LocalDateTime now = LocalDateTime.now();

        // Determine actual values
        Integer actualWaterAmount = request.getWaterAmountMl() != null ?
                request.getWaterAmountMl() : task.getWaterAmountMl();
        String actualFeedingType = request.getFeedingType() != null ?
                request.getFeedingType() : task.getFeedingType();

        // Update task status
        task.setStatus(TaskStatus.COMPLETED);
        task.setCompletedAt(now);
        careTaskMapper.updateById(task);

        // Create care log
        CareLog careLog = CareLog.builder()
                .userPlantId(task.getUserPlantId())
                .careType(task.getCareType())
                .performedAt(now)
                .waterAmountMl(actualWaterAmount)
                .feedingType(actualFeedingType)
                .notes(request.getNotes())
                .build();
        careLogMapper.insert(careLog);

        // Update user plant last care time
        updateLastCareTime(task.getUserPlantId(), task.getCareType(), now);

        // Create next task
        CareTask nextTask = createNextTask(task, now.toLocalDate());

        return TaskDetailResponse.builder()
                .id(task.getId())
                .careType(task.getCareType())
                .scheduledDate(task.getScheduledDate())
                .waterAmountMl(task.getWaterAmountMl())
                .feedingType(task.getFeedingType())
                .status(TaskStatus.COMPLETED)
                .completedAt(now)
                .skipReason(null)
                .nextTaskId(nextTask.getId())
                .nextTaskDate(nextTask.getScheduledDate())
                .build();
    }

    @Override
    @Transactional
    public TaskDetailResponse skipTask(Long userId, Long taskId, SkipTaskRequest request) {
        CareTask task = getTaskOrThrow(userId, taskId);

        if (task.getStatus() != TaskStatus.PENDING) {
            throw new BusinessException(ErrorCode.TASK_ALREADY_PROCESSED);
        }

        LocalDateTime now = LocalDateTime.now();

        // Update task status
        task.setStatus(TaskStatus.SKIPPED);
        task.setCompletedAt(now);
        task.setSkipReason(request.getReason());
        careTaskMapper.updateById(task);

        // Create next task based on original scheduled date
        LocalDate baseDate = task.getScheduledDate();
        if (baseDate.isBefore(LocalDate.now())) {
            baseDate = LocalDate.now();
        }
        CareTask nextTask = createNextTask(task, baseDate);

        return TaskDetailResponse.builder()
                .id(task.getId())
                .careType(task.getCareType())
                .scheduledDate(task.getScheduledDate())
                .waterAmountMl(task.getWaterAmountMl())
                .feedingType(task.getFeedingType())
                .status(TaskStatus.SKIPPED)
                .completedAt(now)
                .skipReason(request.getReason())
                .nextTaskId(nextTask.getId())
                .nextTaskDate(nextTask.getScheduledDate())
                .build();
    }

    @Override
    @Transactional
    public void createInitialTasks(Long userPlantId, Integer wateringInterval, Integer wateringAmount,
                                   Integer feedingInterval, String feedingType) {
        LocalDate today = LocalDate.now();

        // Create watering task
        CareTask waterTask = CareTask.builder()
                .userPlantId(userPlantId)
                .careType(CareType.WATER)
                .scheduledDate(today.plusDays(wateringInterval))
                .waterAmountMl(wateringAmount)
                .status(TaskStatus.PENDING)
                .build();
        careTaskMapper.insert(waterTask);

        // Create feeding task
        CareTask feedTask = CareTask.builder()
                .userPlantId(userPlantId)
                .careType(CareType.FEED)
                .scheduledDate(today.plusDays(feedingInterval))
                .feedingType(feedingType)
                .status(TaskStatus.PENDING)
                .build();
        careTaskMapper.insert(feedTask);
    }

    @Override
    @Transactional
    public void deleteTasksByUserPlantId(Long userPlantId) {
        LambdaUpdateWrapper<CareTask> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(CareTask::getUserPlantId, userPlantId);
        careTaskMapper.delete(wrapper);
    }

    private CareTask getTaskOrThrow(Long userId, Long taskId) {
        CareTask task = careTaskMapper.selectById(taskId);
        if (task == null) {
            throw new ResourceNotFoundException(ErrorCode.TASK_NOT_FOUND);
        }

        // Verify ownership
        UserPlant userPlant = userPlantMapper.selectById(task.getUserPlantId());
        if (userPlant == null || !userPlant.getUserId().equals(userId)) {
            throw new ResourceNotFoundException(ErrorCode.TASK_NOT_FOUND);
        }

        return task;
    }

    private List<CareTaskItemResponse> getTasksForDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        // Get user's active plants
        LambdaQueryWrapper<UserPlant> plantWrapper = new LambdaQueryWrapper<>();
        plantWrapper.eq(UserPlant::getUserId, userId)
                .eq(UserPlant::getActive, true);
        List<UserPlant> userPlants = userPlantMapper.selectList(plantWrapper);

        if (userPlants.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> userPlantIds = userPlants.stream()
                .map(UserPlant::getId)
                .toList();

        // Query tasks
        LambdaQueryWrapper<CareTask> taskWrapper = new LambdaQueryWrapper<>();
        taskWrapper.in(CareTask::getUserPlantId, userPlantIds)
                .eq(CareTask::getStatus, TaskStatus.PENDING);

        if (startDate != null && endDate != null && startDate.equals(endDate)) {
            taskWrapper.eq(CareTask::getScheduledDate, startDate);
        } else {
            if (startDate != null) {
                taskWrapper.ge(CareTask::getScheduledDate, startDate);
            }
            if (endDate != null) {
                taskWrapper.le(CareTask::getScheduledDate, endDate);
            }
        }

        taskWrapper.orderByAsc(CareTask::getScheduledDate);
        List<CareTask> tasks = careTaskMapper.selectList(taskWrapper);

        if (tasks.isEmpty()) {
            return Collections.emptyList();
        }

        // Build maps
        Map<Long, UserPlant> userPlantMap = userPlants.stream()
                .collect(Collectors.toMap(UserPlant::getId, up -> up));

        List<Long> plantIds = userPlants.stream()
                .map(UserPlant::getPlantId)
                .distinct()
                .toList();
        Map<Long, Plant> plantMap = plantMapper.selectBatchIds(plantIds).stream()
                .collect(Collectors.toMap(Plant::getId, p -> p));

        LocalDate today = LocalDate.now();

        return tasks.stream().map(task -> {
            UserPlant userPlant = userPlantMap.get(task.getUserPlantId());
            Plant plant = userPlant != null ? plantMap.get(userPlant.getPlantId()) : null;

            int daysUntil = (int) ChronoUnit.DAYS.between(today, task.getScheduledDate());
            String status = daysUntil < 0 ? "OVERDUE" : "PENDING";

            return CareTaskItemResponse.builder()
                    .id(task.getId())
                    .careType(task.getCareType())
                    .scheduledDate(task.getScheduledDate())
                    .status(status)
                    .daysUntil(daysUntil)
                    .waterAmountMl(task.getWaterAmountMl())
                    .feedingType(task.getFeedingType())
                    .userPlantId(task.getUserPlantId())
                    .plantNickname(userPlant != null ? userPlant.getNickname() : null)
                    .plantImageUrl(userPlant != null ? (userPlant.getPhotoUrl() != null ? userPlant.getPhotoUrl() :
                        (plant != null ? plant.getImageUrl() : null)) : null)
                    .plantLocation(userPlant != null ? userPlant.getLocation() : null)
                    .build();
        }).toList();
    }

    private void updateLastCareTime(Long userPlantId, CareType careType, LocalDateTime time) {
        UserPlant userPlant = userPlantMapper.selectById(userPlantId);
        if (userPlant != null) {
            if (careType == CareType.WATER) {
                userPlant.setLastWateredAt(time);
            } else if (careType == CareType.FEED) {
                userPlant.setLastFedAt(time);
            }
            userPlantMapper.updateById(userPlant);
        }
    }

    private CareTask createNextTask(CareTask completedTask, LocalDate baseDate) {
        UserPlant userPlant = userPlantMapper.selectById(completedTask.getUserPlantId());
        Plant plant = plantMapper.selectById(userPlant.getPlantId());

        int interval;
        Integer waterAmount = null;
        String feedingType = null;

        if (completedTask.getCareType() == CareType.WATER) {
            interval = userPlant.getCustomWateringIntervalDays() != null ?
                    userPlant.getCustomWateringIntervalDays() : plant.getWateringIntervalDays();
            waterAmount = userPlant.getCustomWateringAmountMl() != null ?
                    userPlant.getCustomWateringAmountMl() : plant.getWateringAmountMl();
        } else {
            interval = userPlant.getCustomFeedingIntervalDays() != null ?
                    userPlant.getCustomFeedingIntervalDays() : plant.getFeedingIntervalDays();
            feedingType = userPlant.getCustomFeedingType() != null ?
                    userPlant.getCustomFeedingType() : plant.getFeedingType();
        }

        CareTask nextTask = CareTask.builder()
                .userPlantId(completedTask.getUserPlantId())
                .careType(completedTask.getCareType())
                .scheduledDate(baseDate.plusDays(interval))
                .waterAmountMl(waterAmount)
                .feedingType(feedingType)
                .status(TaskStatus.PENDING)
                .build();

        careTaskMapper.insert(nextTask);
        return nextTask;
    }

}
