package com.soilmate.webservice.service.impl;

import com.soilmate.common.enums.ErrorCode;
import com.soilmate.common.exception.ResourceNotFoundException;
import com.soilmate.webservice.dto.response.PlantResponse;
import com.soilmate.webservice.entity.Barcode;
import com.soilmate.webservice.entity.Plant;
import com.soilmate.webservice.mapper.BarcodeMapper;
import com.soilmate.webservice.mapper.PlantMapper;
import com.soilmate.webservice.service.PlantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlantServiceImpl implements PlantService {

    private final PlantMapper plantMapper;
    private final BarcodeMapper barcodeMapper;

    @Override
    public PlantResponse getPlantById(Long plantId) {
        Plant plant = plantMapper.selectById(plantId);
        if (plant == null) {
            throw new ResourceNotFoundException(ErrorCode.PLANT_NOT_FOUND);
        }
        return toPlantResponse(plant);
    }

    @Override
    public PlantResponse getPlantByBarcode(String barcodeValue) {
        Barcode barcode = barcodeMapper.selectByBarcodeValue(barcodeValue);
        if (barcode == null) {
            throw new ResourceNotFoundException(ErrorCode.BARCODE_NOT_FOUND);
        }

        Plant plant = plantMapper.selectById(barcode.getPlantId());
        if (plant == null) {
            throw new ResourceNotFoundException(ErrorCode.PLANT_NOT_FOUND);
        }

        return toPlantResponse(plant);
    }

    private PlantResponse toPlantResponse(Plant plant) {
        return PlantResponse.builder()
                .id(plant.getId())
                .name(plant.getName())
                .scientificName(plant.getScientificName())
                .wateringIntervalDays(plant.getWateringIntervalDays())
                .wateringAmountMl(plant.getWateringAmountMl())
                .feedingIntervalDays(plant.getFeedingIntervalDays())
                .feedingType(plant.getFeedingType())
                .sunlightRequirement(plant.getSunlightRequirement())
                .imageUrl(plant.getImageUrl())
                .build();
    }
}
