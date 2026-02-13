package com.soilmate.webservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.soilmate.common.enums.CareType;
import com.soilmate.common.enums.ErrorCode;
import com.soilmate.common.exception.ResourceNotFoundException;
import com.soilmate.webservice.dto.request.AddCareLogRequest;
import com.soilmate.webservice.dto.response.CareLogItemResponse;
import com.soilmate.webservice.entity.CareLog;
import com.soilmate.webservice.entity.Plant;
import com.soilmate.webservice.entity.UserPlant;
import com.soilmate.webservice.mapper.CareLogMapper;
import com.soilmate.webservice.mapper.PlantMapper;
import com.soilmate.webservice.mapper.UserPlantMapper;
import com.soilmate.webservice.service.CareLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CareLogServiceImpl implements CareLogService {
    
    private final CareLogMapper careLogMapper;
    private final UserPlantMapper userPlantMapper;
    private final PlantMapper plantMapper;

    @Override
    public List<CareLogItemResponse> getLogsByDate(Long userId, LocalDate date, Long userPlantId) {
        // Get user's active plants
        LambdaQueryWrapper<UserPlant> plantWrapper = new LambdaQueryWrapper<>();
        plantWrapper.eq(UserPlant::getUserId, userId)
                .eq(UserPlant::getIsActive, true);

        if (userPlantId != null) {
            plantWrapper.eq(UserPlant::getId, userPlantId);
        }

        List<UserPlant> userPlants = userPlantMapper.selectList(plantWrapper);
        if (userPlants.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> userPlantIds = userPlants.stream()
                .map(UserPlant::getId)
                .toList();

        // Query logs for the date
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        LambdaQueryWrapper<CareLog> logWrapper = new LambdaQueryWrapper<>();
        logWrapper.in(CareLog::getUserPlantId, userPlantIds)
                .ge(CareLog::getPerformedAt, startOfDay)
                .le(CareLog::getPerformedAt, endOfDay)
                .orderByDesc(CareLog::getPerformedAt);

        List<CareLog> logs = careLogMapper.selectList(logWrapper);

        return buildLogResponses(logs, userPlants);
    }

    @Override
    public List<CareLogItemResponse> getRecentLogs(Long userPlantId, int limit) {
        LambdaQueryWrapper<CareLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CareLog::getUserPlantId, userPlantId)
                .orderByDesc(CareLog::getPerformedAt)
                .last("LIMIT " + limit);

        List<CareLog> logs = careLogMapper.selectList(wrapper);
        if (logs.isEmpty()) {
            return Collections.emptyList();
        }

        UserPlant userPlant = userPlantMapper.selectById(userPlantId);
        return buildLogResponses(logs, List.of(userPlant));
    }

    @Override
    @Transactional
    public CareLogItemResponse addCareLog(Long userId, AddCareLogRequest request) {
        // Verify user plant ownership
        UserPlant userPlant = userPlantMapper.selectById(request.getUserPlantId());
        if (userPlant == null || !userPlant.getUserId().equals(userId) || !userPlant.getIsActive()) {
            throw new ResourceNotFoundException(ErrorCode.USER_PLANT_NOT_FOUND);
        }

        // Create care log
        LocalDateTime performedAt = request.getPerformedAt() != null ? 
                request.getPerformedAt() : LocalDateTime.now();

        CareLog careLog = CareLog.builder()
                .userPlantId(request.getUserPlantId())
                .careType(request.getCareType())
                .performedAt(performedAt)
                .waterAmountMl(request.getWaterAmountMl())
                .feedingType(request.getFeedingType())
                .notes(request.getNotes())
                .build();

        careLogMapper.insert(careLog);

        // Update last care time
        if (request.getCareType() == CareType.WATER) {
            userPlant.setLastWateredAt(performedAt);
        } else if (request.getCareType() == CareType.FEED) {
            userPlant.setLastFedAt(performedAt);
        }
        userPlantMapper.updateById(userPlant);

        // Build response
        Plant plant = plantMapper.selectById(userPlant.getPlantId());
        String imageUrl = userPlant.getPhotoUrl() != null ? userPlant.getPhotoUrl() :
                (plant != null ? plant.getImageUrl() : null);

        return CareLogItemResponse.builder()
                .id(careLog.getId())
                .taskId(null)
                .careType(careLog.getCareType())
                .performedAt(careLog.getPerformedAt())
                .waterAmountMl(careLog.getWaterAmountMl())
                .feedingType(careLog.getFeedingType())
                .notes(careLog.getNotes())
                .userPlantId(userPlant.getId())
                .plantNickname(userPlant.getNickname())
                .plantImageUrl(imageUrl)
                .build();
    }

    @Override
    @Transactional
    public void deleteLogsByUserPlantId(Long userPlantId) {
        LambdaUpdateWrapper<CareLog> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(CareLog::getUserPlantId, userPlantId);
        careLogMapper.delete(wrapper);
    }

    private List<CareLogItemResponse> buildLogResponses(List<CareLog> logs, 
                                                         List<UserPlant> userPlants) {
        if (logs.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, UserPlant> userPlantMap = userPlants.stream()
                .collect(Collectors.toMap(UserPlant::getId, up -> up));

        List<Long> plantIds = userPlants.stream()
                .map(UserPlant::getPlantId)
                .distinct()
                .toList();
        Map<Long, Plant> plantMap = plantMapper.selectBatchIds(plantIds).stream()
                .collect(Collectors.toMap(Plant::getId, p -> p));

        return logs.stream().map(log -> {
            UserPlant userPlant = userPlantMap.get(log.getUserPlantId());
            Plant plant = userPlant != null ? plantMap.get(userPlant.getPlantId()) : null;

            String imageUrl = null;
            String nickname = null;
            if (userPlant != null) {
                nickname = userPlant.getNickname();
                imageUrl = userPlant.getPhotoUrl() != null ? userPlant.getPhotoUrl() :
                        (plant != null ? plant.getImageUrl() : null);
            }

            return CareLogItemResponse.builder()
                    .id(log.getId())
                    .careType(log.getCareType())
                    .performedAt(log.getPerformedAt())
                    .waterAmountMl(log.getWaterAmountMl())
                    .feedingType(log.getFeedingType())
                    .notes(log.getNotes())
                    .userPlantId(log.getUserPlantId())
                    .plantNickname(nickname)
                    .plantImageUrl(imageUrl)
                    .build();
        }).toList();
    }
}
