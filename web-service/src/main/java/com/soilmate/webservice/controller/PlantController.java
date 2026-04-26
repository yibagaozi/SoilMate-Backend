package com.soilmate.webservice.controller;

import com.soilmate.common.response.ApiResponse;
import com.soilmate.common.security.annotation.RequireAuth;
import com.soilmate.webservice.dto.response.PlantResponse;
import com.soilmate.webservice.service.PlantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for plant species endpoints.
 *
 * @author MA, Ruize
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/plants")
@RequiredArgsConstructor
@RequireAuth
public class PlantController {

    private final PlantService plantService;

    @GetMapping("/{id}")
    public ApiResponse<PlantResponse> getPlantById(@PathVariable Long id) {
        return ApiResponse.success(plantService.getPlantById(id));
    }

    @GetMapping("/barcode/{barcodeValue}")
    public ApiResponse<PlantResponse> getPlantByBarcode(@PathVariable String barcodeValue) {
        return ApiResponse.success(plantService.getPlantByBarcode(barcodeValue));
    }
}
