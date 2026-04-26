package com.soilmate.webservice.service;

import com.soilmate.webservice.dto.request.UpdateUserRequest;
import com.soilmate.webservice.dto.response.AvatarResponse;
import com.soilmate.webservice.dto.response.UserProfileResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service interface for user operations.
 *
 * @author MA, Ruize
 * @since 1.0.0
 */
public interface UserService {

    /**
     * Get current user's profile.
     *
     * @return user profile with auth methods and stats
     */
    UserProfileResponse getMyProfile();

    /**
     * Update current user's profile.
     *
     * @param request update request
     * @return updated user info
     */
    UserProfileResponse updateMyProfile(UpdateUserRequest request);

    /**
     * Upload avatar.
     *
     * @param file avatar image file
     * @return avatar URL
     */
    AvatarResponse uploadAvatar(MultipartFile file);
}
