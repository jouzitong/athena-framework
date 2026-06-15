package org.athena.framework.datasource.strategy;

import org.athena.framework.datasource.properties.DynamicDataSourceProperties;
import org.athena.framework.datasource.routing.RouteRequest;

public class TenantRouteStrategy implements RouteStrategy {

    private final DynamicDataSourceProperties properties;

    public TenantRouteStrategy(DynamicDataSourceProperties properties) {
        this.properties = properties;
    }

    @Override
    public String name() {
        return "tenant";
    }

    @Override
    public String determine(RouteRequest request) {
        if (request.getTenantId() == null || request.getTenantId().isBlank()) {
            return null;
        }
        return properties.getTenants().get(request.getTenantId());
    }
}
