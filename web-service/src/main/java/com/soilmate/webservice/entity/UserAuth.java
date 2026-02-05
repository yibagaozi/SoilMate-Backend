package com.soilmate.webservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.soilmate.common.enums.AuthProvider;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity representing the user authentication methods table.
 *
 * @author MA, Ruize
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_auth")
public class UserAuth {

    /**
     * The unique identifier for this authentication record.
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * The ID of the user this authentication method belongs to.
     */
    private Long userId;

    /**
     * The authentication provider for this login method.
     */
    private AuthProvider authProvider;

    /**
     * The unique user identifier from the OAuth provider.
     */
    private String providerUserId;

    /**
     * The email address provided by the OAuth provider.
     */
    private String providerEmail;

    /**
     * The hashed password for email authentication.
     */
    private String passwordHash;

    /**
     * The OAuth access token from the authentication provider.
     */
    private String accessToken;

    /**
     * The OAuth refresh token from the authentication provider.
     */
    private String refreshToken;

    /**
     * The expiration timestamp of the OAuth access token.
     */
    private LocalDateTime tokenExpiresAt;

    /**
     * Whether this is the user's primary login method.
     */
    @Builder.Default
    private Boolean isPrimary = false;

    /**
     * The timestamp when this authentication record was created.
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * The timestamp when this authentication record was last updated.
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
