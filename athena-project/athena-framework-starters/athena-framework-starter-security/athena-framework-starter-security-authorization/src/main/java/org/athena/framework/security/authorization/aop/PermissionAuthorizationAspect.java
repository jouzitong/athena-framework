package org.athena.framework.security.authorization.aop;

import org.athena.framework.security.api.annotation.RequirePermission;
import org.athena.framework.security.api.model.UserContext;
import org.athena.framework.security.api.spi.PermissionEvaluator;
import org.athena.framework.security.auth.core.context.SecurityContextHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class PermissionAuthorizationAspect {

    private final PermissionEvaluator permissionEvaluator;

    public PermissionAuthorizationAspect(PermissionEvaluator permissionEvaluator) {
        this.permissionEvaluator = permissionEvaluator;
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
            throw new SecurityException("permission denied");
        }
        return joinPoint.proceed();
    }
}
