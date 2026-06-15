package org.athena.framework.datasource.strategy;

import org.athena.framework.datasource.properties.DynamicDataSourceProperties;
import org.athena.framework.datasource.routing.RouteRequest;

public class ReadWriteRouteStrategy implements RouteStrategy {

    private final DynamicDataSourceProperties properties;

    public ReadWriteRouteStrategy(DynamicDataSourceProperties properties) {
        this.properties = properties;
    }

    @Override
    public String name() {
        return "readwrite";
    }

    @Override
    public String determine(RouteRequest request) {
        String tenantGroup = request.getTenantId() == null ? null : properties.getTenants().get(request.getTenantId());
        if (tenantGroup == null || tenantGroup.isBlank()) {
            return request.isReadOnly() ? "slave" : properties.getPrimary();
        }
        DynamicDataSourceProperties.GroupConfig groupConfig = properties.getGroups().get(tenantGroup);
        if (groupConfig == null) {
            return tenantGroup;
        }
        if (!request.isReadOnly()) {
            return groupConfig.getMaster();
        }
        if (groupConfig.getSlaves() == null || groupConfig.getSlaves().isEmpty()) {
            return groupConfig.getMaster();
        }
        return groupConfig.getSlaves().get(0);
    }
}
