package com.soilmate.webservice.service;

import com.soilmate.webservice.dto.response.PlantResponse;

public interface PlantService {

    PlantResponse getPlantById(Long plantId);

    PlantResponse getPlantByBarcode(String barcodeValue);
}
