package org.athena.framework.security.authorization.aop;

import org.athena.framework.security.api.annotation.RequirePermission;
import org.athena.framework.security.api.event.AuthorizationDecisionEvent;
import org.athena.framework.security.api.model.UserContext;
import org.athena.framework.security.api.spi.PermissionEvaluator;
import org.athena.framework.security.auth.core.context.SecurityContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationEventPublisher;

/**
 * 权限校验切面。
 * 在命中 {@link RequirePermission} 注解的方法前执行权限判定。
 */
@Aspect
public class PermissionAuthorizationAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionAuthorizationAspect.class);

    private final PermissionEvaluator permissionEvaluator;

    private final ApplicationEventPublisher eventPublisher;

    public PermissionAuthorizationAspect(PermissionEvaluator permissionEvaluator,
                                         ApplicationEventPublisher eventPublisher) {
        this.permissionEvaluator = permissionEvaluator;
        this.eventPublisher = eventPublisher;
    }

    @Around("@annotation(requirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, RequirePermission requirePermission) throws Throwable {
        UserContext userContext = SecurityContextHolder.get();
        String[] permissions = requirePermission.value();
        boolean hasPermission;

        if (requirePermission.requireAll()) {
            hasPermission = true;
            for (String permission : permissions) {
                if (!permissionEvaluator.hasPermission(userContext, permission)) {
                    hasPermission = false;
                    break;
                }
            }
        } else {
            hasPermission = false;
            for (String permission : permissions) {
                if (permissionEvaluator.hasPermission(userContext, permission)) {
                    hasPermission = true;
                    break;
                }
            }
        }

        if (!hasPermission) {
            LOGGER.warn("Permission denied, method={}, userId={}, requiredPermissions={}, requireAll={}",
                joinPoint.getSignature().toShortString(),
                userContext != null && userContext.subject() != null ? userContext.subject().userId() : null,
                String.join(",", permissions),
                requirePermission.requireAll());
            eventPublisher.publishEvent(new AuthorizationDecisionEvent(
                userContext,
                joinPoint.getSignature().toShortString(),
                permissions,
                requirePermission.requireAll(),
                false,
                "permission denied"
            ));
            throw new SecurityException("permission denied");
        }
        eventPublisher.publishEvent(new AuthorizationDecisionEvent(
            userContext,
            joinPoint.getSignature().toShortString(),
            permissions,
            requirePermission.requireAll(),
            true,
            "permission granted"
        ));
        return joinPoint.proceed();
    }
}
