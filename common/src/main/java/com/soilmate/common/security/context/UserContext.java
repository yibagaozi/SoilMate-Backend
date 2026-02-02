package com.soilmate.common.security.context;

import com.soilmate.common.enums.UserRole;
import lombok.*;

/**
 * Holds current authenticated user's information.
 *
 * @author MA, Ruize
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserContext {

    private Long userId;
    private String email;
    private String displayName;
    private UserRole role;

    public boolean isAdmin() {
        return UserRole.ADMIN.equals(this.role);
    }
}
