package com.soilmate.common.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT configuration properties.
 *
 * @author MA, Ruize
 * @since 1.0.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "soilmate.jwt")
public class JwtProperties {

    /**
     * Secret key for signing tokens.
     */
    private String secret;

    /**
     * Access token expiration in seconds. Default: 3600 (1 hour).
     */
    private Long accessTokenExpiration = 3600L;

    /**
     * Refresh token expiration in seconds. Default: 2592000 (30 days).
     */
    private Long refreshTokenExpiration = 2592000L;

    public static final String ISSUER = "SoilMate";
    public static final String HEADER_NAME = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
}
