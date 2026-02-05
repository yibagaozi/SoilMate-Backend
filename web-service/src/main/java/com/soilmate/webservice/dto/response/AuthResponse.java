package com.soilmate.webservice.dto.response;

import com.soilmate.common.security.dto.TokenResponse;
import lombok.*;

/**
 * Response DTO for authentication operations (register/login).
 * Combines user information with JWT tokens.
 *
 * @author MA, Ruize
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private UserResponse user;
    private TokenResponse tokens;
    
}
