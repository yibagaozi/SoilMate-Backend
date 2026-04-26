package com.soilmate.webservice.dto.request;

import com.soilmate.common.enums.Region;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * Request DTO for updating user profile.
 *
 * @author MA, Ruize
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    @Size(max = 255, message = "Display name must not exceed 255 characters")
    private String displayName;

    @Size(max = 255, message = "Timezone must not exceed 255 characters")
    private String timezone;

    private Boolean notificationEnabled;

    private LocalTime reminderTime;

    private Region region;
}
