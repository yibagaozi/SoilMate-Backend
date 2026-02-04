package com.soilmate.common.security.interceptor;

import com.soilmate.common.enums.ErrorCode;
import com.soilmate.common.exception.AccessDeniedException;
import com.soilmate.common.exception.TokenException;
import com.soilmate.common.security.annotation.RequireAdmin;
import com.soilmate.common.security.annotation.RequireAuth;
import com.soilmate.common.security.config.JwtProperties;
import com.soilmate.common.security.context.UserContext;
import com.soilmate.common.security.context.UserContextHolder;
import com.soilmate.common.security.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT authentication interceptor.
 *
 * @author MA, Ruize
 * @since 1.0.0
 */
@Component
public class JwtAuthenticationInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                             @NonNull Object handler) throws Exception {

        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        boolean requiresAuth = isAuthRequired(handlerMethod);
        boolean requiresAdmin = isAdminRequired(handlerMethod);

        String header = request.getHeader(JwtProperties.HEADER_NAME);
        String token = jwtUtil.extractTokenFromHeader(header);

        if (requiresAuth || requiresAdmin) {
            if (token == null) {
                throw new TokenException(ErrorCode.TOKEN_MISSING);
            }

            try {
                UserContext userContext = jwtUtil.validateAccessToken(token);
                UserContextHolder.setContext(userContext);

                if (requiresAdmin && !userContext.isAdmin()) {
                    throw new AccessDeniedException(ErrorCode.ACCESS_DENIED);
                }
            } catch (AccessDeniedException | TokenException e) {
                UserContextHolder.clear();
                throw e;
            }
        } else {
            if (token != null) {
                try {
                    UserContext userContext = jwtUtil.validateAccessToken(token);
                    UserContextHolder.setContext(userContext);
                } catch (TokenException ignored) {
                }
            }
        }

        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                @NonNull Object handler, Exception ex) throws Exception {
        UserContextHolder.clear();
    }

    private boolean isAuthRequired(HandlerMethod handlerMethod) {
        return handlerMethod.getMethodAnnotation(RequireAuth.class) != null
                || handlerMethod.getBeanType().getAnnotation(RequireAuth.class) != null;
    }

    private boolean isAdminRequired(HandlerMethod handlerMethod) {
        return handlerMethod.getMethodAnnotation(RequireAdmin.class) != null
                || handlerMethod.getBeanType().getAnnotation(RequireAdmin.class) != null;
    }

}
