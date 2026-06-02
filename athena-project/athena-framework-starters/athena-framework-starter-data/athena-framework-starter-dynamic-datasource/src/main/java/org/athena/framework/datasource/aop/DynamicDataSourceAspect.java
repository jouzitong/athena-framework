package org.athena.framework.datasource.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.athena.framework.datasource.annotation.DataSourceRoute;
import org.athena.framework.datasource.context.RouteContext;
import org.athena.framework.datasource.context.TenantContext;
import org.athena.framework.datasource.routing.RouteDecisionEngine;
import org.athena.framework.datasource.routing.RouteRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;

@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DynamicDataSourceAspect {

    private final RouteDecisionEngine routeDecisionEngine;

    public DynamicDataSourceAspect(RouteDecisionEngine routeDecisionEngine) {
        this.routeDecisionEngine = routeDecisionEngine;
    }

    @Around("execution(* *(..)) && (@within(org.springframework.stereotype.Service) || @within(org.apache.ibatis.annotations.Mapper) || @annotation(org.springframework.transaction.annotation.Transactional))")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Class<?> targetClass = point.getTarget() == null ? signature.getDeclaringType() : point.getTarget().getClass();

        String explicitRoute = extractExplicitRoute(method, targetClass);
        String routeKey = explicitRoute;
        if (routeKey == null || routeKey.isBlank()) {
            RouteRequest request = RouteRequest.builder()
                .targetClass(targetClass)
                .methodName(method.getName())
                .tenantId(TenantContext.getTenantId())
                .readOnly(isReadOnly(method, targetClass))
                .build();
            routeKey = routeDecisionEngine.determine(request);
        }

        if (routeKey != null && !routeKey.isBlank()) {
            RouteContext.push(routeKey);
        }
        try {
            return point.proceed();
        } finally {
            if (routeKey != null && !routeKey.isBlank()) {
                RouteContext.poll();
            }
        }
    }

    private String extractExplicitRoute(Method method, Class<?> targetClass) {
        DataSourceRoute methodRoute = method.getAnnotation(DataSourceRoute.class);
        if (methodRoute != null) {
            return methodRoute.value();
        }
        DataSourceRoute classRoute = targetClass.getAnnotation(DataSourceRoute.class);
        if (classRoute != null) {
            return classRoute.value();
        }
        return null;
    }

    private boolean isReadOnly(Method method, Class<?> targetClass) {
        Transactional methodTx = method.getAnnotation(Transactional.class);
        if (methodTx != null) {
            return methodTx.readOnly();
        }
        Transactional classTx = targetClass.getAnnotation(Transactional.class);
        if (classTx != null) {
            return classTx.readOnly();
        }
        return method.getName().startsWith("query") || method.getName().startsWith("get") || method.getName().startsWith("find");
    }
}
