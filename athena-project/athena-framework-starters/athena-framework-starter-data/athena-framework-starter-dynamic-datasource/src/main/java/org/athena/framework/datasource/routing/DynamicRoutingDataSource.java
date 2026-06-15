package org.athena.framework.datasource.routing;

import org.athena.framework.datasource.context.RouteContext;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynamicRoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        return RouteContext.peek();
    }
}
