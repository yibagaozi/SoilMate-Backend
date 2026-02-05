package com.soilmate.webservice.service.impl;

import com.soilmate.webservice.dto.request.UpdateUserRequest;
import com.soilmate.webservice.dto.response.AvatarResponse;
import com.soilmate.webservice.dto.response.UserProfileResponse;
import com.soilmate.webservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    @Override
    public UserProfileResponse getMyProfile() {
        return null;
    }

    @Override
    public UserProfileResponse updateMyProfile(UpdateUserRequest request) {
        return null;
    }

    @Override
    public AvatarResponse uploadAvatar(MultipartFile file) {
        return null;
    }
}
