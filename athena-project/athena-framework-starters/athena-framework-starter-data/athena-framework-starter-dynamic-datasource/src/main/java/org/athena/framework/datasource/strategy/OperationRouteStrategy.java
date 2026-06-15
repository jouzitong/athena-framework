package org.athena.framework.datasource.strategy;

import org.athena.framework.datasource.properties.DynamicDataSourceProperties;
import org.athena.framework.datasource.routing.RouteRequest;
import org.springframework.util.AntPathMatcher;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class OperationRouteStrategy implements RouteStrategy {

    private final AntPathMatcher matcher = new AntPathMatcher();

    private final List<DynamicDataSourceProperties.OperationRouteRule> rules;

    public OperationRouteStrategy(DynamicDataSourceProperties properties) {
        this.rules = properties.getOperationRoutes().stream()
            .sorted(Comparator.comparing(DynamicDataSourceProperties.OperationRouteRule::getPriority))
            .collect(Collectors.toList());
    }

    @Override
    public String name() {
        return "operation";
    }

    @Override
    public String determine(RouteRequest request) {
        if (rules.isEmpty()) {
            return null;
        }
        String className = request.getTargetClass() == null ? "" : request.getTargetClass().getName();
        String classAndMethod = className + "#" + request.getMethodName();
        for (DynamicDataSourceProperties.OperationRouteRule rule : rules) {
            if (rule.getTenant() != null && !rule.getTenant().equals(request.getTenantId())) {
                continue;
            }
            if (matches(rule.getService(), classAndMethod)
                || matches(rule.getMapper(), classAndMethod)
                || matchesMethodOnly(rule.getMethod(), request.getMethodName())) {
                return rule.getTarget();
            }
        }
        return null;
    }

    private boolean matches(String pattern, String value) {
        return pattern != null && matcher.match(pattern, value);
    }

    private boolean matchesMethodOnly(String pattern, String methodName) {
        return pattern != null && matcher.match(pattern, methodName);
    }
}
