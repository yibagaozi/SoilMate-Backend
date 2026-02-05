package com.soilmate.common.security.annotation;

import java.lang.annotation.*;

/**
 * Marks endpoints that require user authentication.
 *
 * @author MA, Ruize
 * @since 1.0.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireAuth {
}
