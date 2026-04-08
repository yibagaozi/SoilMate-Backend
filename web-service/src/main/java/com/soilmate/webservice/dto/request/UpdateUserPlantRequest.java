package com.soilmate.webservice.dto.request;

import com.soilmate.common.enums.GrowingEnvironment;
import com.soilmate.common.enums.PotSize;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating a user plant.
 *
 * @author MA, Ruize
 * @since 1.0.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserPlantRequest {

    @Size(max = 100, message = "Nickname must not exceed 100 characters")
    private String nickname;

    @Size(max = 100, message = "Location must not exceed 100 characters")
    private String location;

    private String notes;

    private Integer wateringIntervalDays;
    private Integer wateringAmountMl;
    private Integer feedingIntervalDays;
    private String feedingType;

    private PotSize potSize;
    private GrowingEnvironment environment;

}
