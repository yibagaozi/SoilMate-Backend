package com.soilmate.webservice.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for adding a plant to user's garden.
 *
 * @author MA, Ruize
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddUserPlantRequest {

    /**
     * Barcode value (use this OR plantId).
     */
    private String barcodeValue;

    /**
     * Plant ID (use this OR barcodeValue).
     */
    private Long plantId;

    @Size(max = 100, message = "Nickname must not exceed 100 characters")
    private String nickname;

    @Size(max = 100, message = "Location must not exceed 100 characters")
    private String location;

    private String notes;

    private Integer wateringIntervalDays;
    private Integer wateringAmountMl;
    private Integer feedingIntervalDays;

}
