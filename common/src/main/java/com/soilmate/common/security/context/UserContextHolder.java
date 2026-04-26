package com.soilmate.common.security.context;

/**
 * Thread-local holder for current user's security context.
 *
 * @author MA, Ruize
 * @since 1.0.0
 */
public class UserContextHolder {

    private static final ThreadLocal<UserContext> CONTEXT = new ThreadLocal<>();

    private UserContextHolder() {
    }

    public static void setContext(UserContext context) {
        CONTEXT.set(context);
    }

    public static UserContext getContext() {
        return CONTEXT.get();
    }

    public static Long getCurrentUserId() {
        UserContext context = CONTEXT.get();
        return context != null ? context.getUserId() : null;
    }

    public static String getCurrentUserEmail() {
        UserContext context = CONTEXT.get();
        return context != null ? context.getEmail() : null;
    }

    public static boolean isAuthenticated() {
        return CONTEXT.get() != null;
    }

    public static boolean isAdmin() {
        UserContext context = CONTEXT.get();
        return context != null && context.isAdmin();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
