package com.soilmate.webservice.service;

import com.soilmate.webservice.dto.request.AddUserPlantRequest;
import com.soilmate.webservice.dto.request.UpdateUserPlantRequest;
import com.soilmate.webservice.dto.response.UserPlantDetailResponse;

import java.util.List;

public interface UserPlantService {

    Long addUserPlant(Long userId, AddUserPlantRequest request);

    void updateUserPlant(Long userId, Long userPlantId, UpdateUserPlantRequest request);

    List<UserPlantDetailResponse> getUserPlants(Long userId);

    UserPlantDetailResponse getUserPlantDetail(Long userId, Long userPlantId);

    void deleteUserPlant(Long userId, Long userPlantId);
}
