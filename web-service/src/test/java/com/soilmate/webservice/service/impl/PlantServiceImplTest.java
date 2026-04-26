package com.soilmate.webservice.service.impl;

import com.soilmate.common.enums.SunlightRequirement;
import com.soilmate.common.exception.ResourceNotFoundException;
import com.soilmate.webservice.dto.response.PlantResponse;
import com.soilmate.webservice.entity.Barcode;
import com.soilmate.webservice.entity.Plant;
import com.soilmate.webservice.mapper.BarcodeMapper;
import com.soilmate.webservice.mapper.PlantMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlantServiceImplTest {

    @Mock
    private PlantMapper plantMapper;

    @Mock
    private BarcodeMapper barcodeMapper;

    @InjectMocks
    private PlantServiceImpl plantService;

    private Plant testPlant;
    private Barcode testBarcode;

    @BeforeEach
    void setUp() {
        testPlant = Plant.builder()
                .id(1L)
                .name("Monstera")
                .scientificName("Monstera deliciosa")
                .wateringIntervalDays(7)
                .wateringAmountMl(300)
                .feedingIntervalDays(30)
                .feedingType("liquid fertilizer")
                .sunlightRequirement(SunlightRequirement.LOW)
                .imageUrl("https://example.com/monstera.jpg")
                .note("Keep away from direct sunlight")
                .build();

        testBarcode = Barcode.builder()
                .id(1L)
                .barcodeValue("PLT-00001-A3")
                .plantId(1L)
                .batchNumber("BATCH-001")
                .build();
    }

    // ==================== getPlantById ====================

    @Test
    void getPlantById_success_returnsPlantResponse() {
        when(plantMapper.selectById(1L)).thenReturn(testPlant);

        PlantResponse response = plantService.getPlantById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Monstera", response.getName());
        assertEquals("Monstera deliciosa", response.getScientificName());
        assertEquals(7, response.getWateringIntervalDays());
        assertEquals(300, response.getWateringAmountMl());
        assertEquals(30, response.getFeedingIntervalDays());
        assertEquals("liquid fertilizer", response.getFeedingType());
        assertEquals(SunlightRequirement.LOW, response.getSunlightRequirement());
        assertEquals("https://example.com/monstera.jpg", response.getImageUrl());
        assertEquals("Keep away from direct sunlight", response.getNote());
    }

    @Test
    void getPlantById_notFound_throwsResourceNotFoundException() {
        when(plantMapper.selectById(999L)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> plantService.getPlantById(999L));
    }

    // ==================== getPlantByBarcode ====================

    @Test
    void getPlantByBarcode_success_returnsPlantResponse() {
        when(barcodeMapper.selectByBarcodeValue("PLT-00001-A3")).thenReturn(testBarcode);
        when(plantMapper.selectById(1L)).thenReturn(testPlant);

        PlantResponse response = plantService.getPlantByBarcode("PLT-00001-A3");

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Monstera", response.getName());
    }

    @Test
    void getPlantByBarcode_barcodeNotFound_throwsResourceNotFoundException() {
        when(barcodeMapper.selectByBarcodeValue("PLT-99999-ZZ")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class,
                () -> plantService.getPlantByBarcode("PLT-99999-ZZ"));

        verify(plantMapper, never()).selectById(anyLong());
    }

    @Test
    void getPlantByBarcode_plantNotFound_throwsResourceNotFoundException() {
        Barcode orphanBarcode = Barcode.builder()
                .id(2L)
                .barcodeValue("PLT-00002-B4")
                .plantId(999L)
                .build();

        when(barcodeMapper.selectByBarcodeValue("PLT-00002-B4")).thenReturn(orphanBarcode);
        when(plantMapper.selectById(999L)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class,
                () -> plantService.getPlantByBarcode("PLT-00002-B4"));
    }
}