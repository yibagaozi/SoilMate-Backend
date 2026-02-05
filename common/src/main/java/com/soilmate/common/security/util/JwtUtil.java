package com.soilmate.common.security.util;

import com.soilmate.common.enums.ErrorCode;
import com.soilmate.common.enums.TokenType;
import com.soilmate.common.enums.UserRole;
import com.soilmate.common.exception.TokenException;
import com.soilmate.common.security.config.JwtProperties;
import com.soilmate.common.security.context.UserContext;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for JWT token operations.
 *
 * @author MA, Ruize
 * @since 1.0.0
 */
@Component
public class JwtUtil {

    private static final String CLAIM_USER_ID = "userId";
    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_DISPLAY_NAME = "displayName";
    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_TOKEN_TYPE = "tokenType";

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * Generates an access token (short-lived).
     */
    public String generateAccessToken(Long userId, String email, String displayName, UserRole role) {
        return generateToken(userId, email, displayName, role,
                TokenType.ACCESS, jwtProperties.getAccessTokenExpiration());
    }

    /**
     * Generates a refresh token (long-lived).
     */
    public String generateRefreshToken(Long userId, String email, String displayName, UserRole role) {
        return generateToken(userId, email, displayName, role,
                TokenType.REFRESH, jwtProperties.getRefreshTokenExpiration());
    }

    /**
     * Generates both access and refresh tokens.
     */
    public Map<String, String> generateTokenPair(Long userId, String email, String displayName, UserRole role) {
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", generateAccessToken(userId, email, displayName, role));
        tokens.put("refreshToken", generateRefreshToken(userId, email, displayName, role));
        return tokens;
    }

    /**
     * Validates access token and returns user context.
     */
    public UserContext validateAccessToken(String token) {
        Claims claims = parseToken(token);

        String tokenType = claims.get(CLAIM_TOKEN_TYPE, String.class);
        if (!TokenType.ACCESS.getCode().equals(tokenType)) {
            throw new TokenException(ErrorCode.TOKEN_TYPE_INVALID);
        }

        return buildUserContext(claims);
    }

    /**
     * Validates refresh token and returns user context.
     */
    public UserContext validateRefreshToken(String token) {
        try {
            Claims claims = parseToken(token);

            String tokenType = claims.get(CLAIM_TOKEN_TYPE, String.class);
            if (!TokenType.REFRESH.getCode().equals(tokenType)) {
                throw new TokenException(ErrorCode.REFRESH_TOKEN_INVALID);
            }

            return buildUserContext(claims);
        } catch (TokenException e) {
            if (e.getErrorCode() == ErrorCode.TOKEN_EXPIRED) {
                throw new TokenException(ErrorCode.REFRESH_TOKEN_EXPIRED);
            }
            throw new TokenException(ErrorCode.REFRESH_TOKEN_INVALID, e);
        }
    }

    /**
     * Extracts user ID.
     */
    public Long extractUserIdSafely(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.get(CLAIM_USER_ID, Long.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Extracts token from Authorization header.
     */
    public String extractTokenFromHeader(String header) {
        if (header != null && header.startsWith(JwtProperties.TOKEN_PREFIX)) {
            return header.substring(JwtProperties.TOKEN_PREFIX.length());
        }
        return null;
    }

    public Long getAccessTokenExpiration() {
        return jwtProperties.getAccessTokenExpiration();
    }

    public Long getRefreshTokenExpiration() {
        return jwtProperties.getRefreshTokenExpiration();
    }

    private String generateToken(Long userId, String email, String displayName,
                                  UserRole role, TokenType tokenType, Long expirationSeconds) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationSeconds * 1000);

        return Jwts.builder()
                .issuer(JwtProperties.ISSUER)
                .subject(String.valueOf(userId))
                .claim(CLAIM_USER_ID, userId)
                .claim(CLAIM_EMAIL, email)
                .claim(CLAIM_DISPLAY_NAME, displayName)
                .claim(CLAIM_ROLE, role.getCode())
                .claim(CLAIM_TOKEN_TYPE, tokenType.getCode())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    private Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new TokenException(ErrorCode.TOKEN_EXPIRED, e);
        } catch (MalformedJwtException e) {
            throw new TokenException(ErrorCode.TOKEN_MALFORMED, e);
        } catch (SignatureException e) {
            throw new TokenException(ErrorCode.TOKEN_SIGNATURE_INVALID, e);
        } catch (JwtException e) {
            throw new TokenException(ErrorCode.TOKEN_INVALID, e);
        }
    }

    private UserContext buildUserContext(Claims claims) {
        return UserContext.builder()
                .userId(claims.get(CLAIM_USER_ID, Long.class))
                .email(claims.get(CLAIM_EMAIL, String.class))
                .displayName(claims.get(CLAIM_DISPLAY_NAME, String.class))
                .role(UserRole.fromCode(claims.get(CLAIM_ROLE, String.class)))
                .build();
    }
}
