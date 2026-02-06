package com.soilmate.webservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.soilmate.common.enums.ErrorCode;
import com.soilmate.common.exception.UserException;
import com.soilmate.common.security.context.UserContextHolder;
import com.soilmate.webservice.dto.request.UpdateUserRequest;
import com.soilmate.webservice.dto.response.AvatarResponse;
import com.soilmate.webservice.dto.response.UserProfileResponse;
import com.soilmate.webservice.entity.User;
import com.soilmate.webservice.entity.UserAuth;
import com.soilmate.webservice.mapper.UserAuthMapper;
import com.soilmate.webservice.mapper.UserMapper;
import com.soilmate.webservice.mapper.UserPlantMapper;
import com.soilmate.webservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserAuthMapper userAuthMapper;
    private final UserPlantMapper userPlantMapper;

    @Override
    public UserProfileResponse getMyProfile() {
        Long userId = UserContextHolder.getCurrentUserId();
        log.info("Getting profile for user: {}", userId);

        // Get user entity
        User user = getUserById(userId);

        // Get auth methods
        List<UserAuth> authMethods = userAuthMapper.selectList(
                new LambdaQueryWrapper<UserAuth>()
                        .eq(UserAuth::getUserId, userId)
        );

        // Get plant stats
        Integer totalPlants = userPlantMapper.countTotalPlants(userId);
        Integer activePlants = userPlantMapper.countActivePlants(userId);

        log.debug("User {} has {} total plants, {} active", userId, totalPlants, activePlants);

        return UserProfileResponse.fromEntity(user, authMethods, totalPlants, activePlants);
    }

    @Override
    @Transactional
    public UserProfileResponse updateMyProfile(UpdateUserRequest request) {
        Long userId = UserContextHolder.getCurrentUserId();
        log.info("Updating profile for user: {}", userId);

        User user = getUserById(userId);

        // Update fields if provided
        if (request.getDisplayName() != null) {
            user.setDisplayName(request.getDisplayName());
        }
        if (request.getTimezone() != null) {
            user.setTimezone(request.getTimezone());
        }
        if (request.getNotificationEnabled() != null) {
            user.setNotificationEnabled(request.getNotificationEnabled());
        }
        if (request.getReminderTime() != null) {
            user.setReminderTime(request.getReminderTime());
        }

        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);

        log.info("Profile updated for user: {}", userId);

        // Return full profile (reuse getMyProfile logic)
        return getMyProfile();
    }

    @Override
    public AvatarResponse uploadAvatar(MultipartFile file) {
        return null;
    }

    /**
     * Get user by ID or throw exception.
     */
    private User getUserById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            log.warn("User not found: {}", userId);
            throw new UserException(ErrorCode.USER_NOT_FOUND);
        }
        return user;
    }

}
