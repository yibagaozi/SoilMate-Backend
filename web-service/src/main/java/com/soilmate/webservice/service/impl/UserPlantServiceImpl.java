package com.soilmate.webservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.soilmate.common.enums.CareType;
import com.soilmate.common.enums.ErrorCode;
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
import com.soilmate.webservice.service.UserPlantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserPlantServiceImpl implements UserPlantService {

    private final UserPlantMapper userPlantMapper;
    private final PlantMapper plantMapper;
    private final BarcodeMapper barcodeMapper;
    private final CareTaskMapper careTaskMapper;
    private final CareTaskService careTaskService;
    private final CareLogService careLogService;

    @Override
    @Transactional
    public Long addUserPlant(Long userId, AddUserPlantRequest request) {

        // Verify plant exists
        Plant plant = plantMapper.selectById(request.getPlantId());
        if (plant == null) {
            throw new ResourceNotFoundException(ErrorCode.PLANT_NOT_FOUND);
        }

        // Calculate effective values
        int wateringInterval = getEffectiveValue(request.getWateringIntervalDays(),
                plant.getWateringIntervalDays());
        int wateringAmount = getEffectiveValue(request.getWateringAmountMl(),
                plant.getWateringAmountMl());
        int feedingInterval = getEffectiveValue(request.getFeedingIntervalDays(),
                plant.getFeedingIntervalDays());
        String feedingType = getEffectiveValue(request.getFeedingType(),
                plant.getFeedingType());

        // Create user plant
        UserPlant userPlant = UserPlant.builder()
                .userId(userId)
                .plantId(request.getPlantId())
                .nickname(request.getNickname())
                .location(request.getLocation())
                .notes(request.getNotes())
                .customWateringIntervalDays(wateringInterval)
                .customWateringAmountMl(wateringAmount)
                .customFeedingIntervalDays(feedingInterval)
                .customFeedingType(feedingType)
                .isActive(true)
                .build();

        userPlantMapper.insert(userPlant);

        // Create initial tasks
        careTaskService.createInitialTasks(userPlant.getId(), wateringInterval, wateringAmount,
                feedingInterval, feedingType);

        return userPlant.getId();
    }

    @Override
    @Transactional
    public void updateUserPlant(Long userId, Long userPlantId, UpdateUserPlantRequest request) {
        UserPlant userPlant = getUserPlantOrThrow(userId, userPlantId);

        // Update fields if provided
        if (request.getNickname() != null) {
            userPlant.setNickname(request.getNickname());
        }
        if (request.getLocation() != null) {
            userPlant.setLocation(request.getLocation());
        }
        if (request.getNotes() != null) {
            userPlant.setNotes(request.getNotes());
        }
        if (request.getWateringIntervalDays() != null) {
            userPlant.setCustomWateringIntervalDays(request.getWateringIntervalDays());
        }
        if (request.getWateringAmountMl() != null) {
            userPlant.setCustomWateringAmountMl(request.getWateringAmountMl());
        }
        if (request.getFeedingType() != null) {
            userPlant.setCustomFeedingType(request.getFeedingType());
        }
        if (request.getFeedingType() != null) {
            userPlant.setCustomFeedingType(request.getFeedingType());
        }

        userPlantMapper.updateById(userPlant);
    }

    @Override
    public List<UserPlantDetailResponse> getUserPlants(Long userId) {
        LambdaQueryWrapper<UserPlant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPlant::getUserId, userId)
                .eq(UserPlant::getIsActive, true)
                .orderByDesc(UserPlant::getCreatedAt);

        List<UserPlant> userPlants = userPlantMapper.selectList(wrapper);
        if (userPlants.isEmpty()) {
            return Collections.emptyList();
        }

        // Get plant info
        List<Long> plantIds = userPlants.stream()
                .map(UserPlant::getPlantId)
                .distinct()
                .toList();
        Map<Long, Plant> plantMap = plantMapper.selectBatchIds(plantIds).stream()
                .collect(Collectors.toMap(Plant::getId, p -> p));

        // Get next task dates
        List<Long> userPlantIds = userPlants.stream()
                .map(UserPlant::getId)
                .toList();
        Map<Long, LocalDate> nextWateringMap = getNextTaskDates(userPlantIds, CareType.WATER.name());
        Map<Long, LocalDate> nextFeedingMap = getNextTaskDates(userPlantIds, CareType.FEED.name());

        return userPlants.stream().map(up -> {
            Plant plant = plantMap.get(up.getPlantId());
            return toDetailResponse(up, plant,
                    nextWateringMap.get(up.getId()),
                    nextFeedingMap.get(up.getId()),
                    null);
        }).toList();
    }

    @Override
    public UserPlantDetailResponse getUserPlantDetail(Long userId, Long userPlantId) {
        UserPlant userPlant = getUserPlantOrThrow(userId, userPlantId);
        Plant plant = plantMapper.selectById(userPlant.getPlantId());

        LocalDate nextWateringDate = careTaskMapper.selectNextTaskDate(userPlantId, CareType.WATER.name());
        LocalDate nextFeedingDate = careTaskMapper.selectNextTaskDate(userPlantId, CareType.FEED.name());
        List<CareLogItemResponse> recentLogs = careLogService.getRecentLogs(userPlantId, 3);

        return toDetailResponse(userPlant, plant, nextWateringDate, nextFeedingDate, recentLogs);
    }

    @Override
    @Transactional
    public void deleteUserPlant(Long userId, Long userPlantId) {
        UserPlant userPlant = getUserPlantOrThrow(userId, userPlantId);

        // Soft delete
        userPlant.setIsActive(false);
        userPlantMapper.updateById(userPlant);

        // Delete related tasks and logs
        careTaskService.deleteTasksByUserPlantId(userPlantId);
        careLogService.deleteLogsByUserPlantId(userPlantId);
    }

    private UserPlant getUserPlantOrThrow(Long userId, Long userPlantId) {
        UserPlant userPlant = userPlantMapper.selectById(userPlantId);
        if (userPlant == null || !userPlant.getUserId().equals(userId) || !userPlant.getIsActive()) {
            throw new ResourceNotFoundException(ErrorCode.USER_PLANT_NOT_FOUND);
        }
        return userPlant;
    }

    private <T> T getEffectiveValue(T customValue, T defaultValue) {
        return customValue != null ? customValue : defaultValue;
    }

    private UserPlantDetailResponse toDetailResponse(UserPlant userPlant, Plant plant, LocalDate nextWateringDate,
                                                     LocalDate nextFeedingDate, List<CareLogItemResponse> recentCareLogs) {
        return UserPlantDetailResponse.builder()
                .id(userPlant.getId())
                .nickname(userPlant.getNickname())
                .location(userPlant.getLocation())
                .photoUrl(userPlant.getPhotoUrl())
                .notes(userPlant.getNotes())
                .lastWateredAt(userPlant.getLastWateredAt())
                .lastFedAt(userPlant.getLastFedAt())
                .createdAt(userPlant.getCreatedAt())
                .plantId(plant.getId())
                .plantName(plant.getName())
                .scientificName(plant.getScientificName())
                .sunlightRequirement(plant.getSunlightRequirement())
                .plantImageUrl(plant.getImageUrl())
                .wateringIntervalDays(userPlant.getCustomWateringIntervalDays())
                .wateringAmountMl(userPlant.getCustomWateringAmountMl())
                .feedingIntervalDays(userPlant.getCustomFeedingIntervalDays())
                .feedingType(userPlant.getCustomFeedingType())
                .nextWateringDate(nextWateringDate)
                .nextFeedingDate(nextFeedingDate)
                .recentCareLogs(recentCareLogs)
                .build();
    }

    private Map<Long, LocalDate> getNextTaskDates(List<Long> userPlantIds, String careType) {
        if (userPlantIds.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Long, Map<String, Object>> rawResult = careTaskMapper.selectNextTaskDatesRaw(userPlantIds, careType);

        Map<Long, LocalDate> result = new HashMap<>();
        for (Map.Entry<Long, Map<String, Object>> entry : rawResult.entrySet()) {
            Object dateObj = entry.getValue().get("scheduledDate");
            if (dateObj != null) {
                if (dateObj instanceof java.sql.Date) {
                    result.put(entry.getKey(), ((java.sql.Date) dateObj).toLocalDate());
                } else {
                    result.put(entry.getKey(), (LocalDate) dateObj);
                }
            }
        }
        return result;
    }

}
