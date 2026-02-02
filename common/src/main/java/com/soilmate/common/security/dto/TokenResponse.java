package com.soilmate.common.security.dto;

import lombok.*;

/**
 * DTO for token responses after successful authentication.
 *
 * @author MA, Ruize
 * @since 1.0.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenResponse {

    private String accessToken;
    private String refreshToken;
    private Long expiresIn;

}
