package org.athena.framework.datasource.routing;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RouteRequest {

    private Class<?> targetClass;

    private String methodName;

    private boolean readOnly;

    private String tenantId;
}
