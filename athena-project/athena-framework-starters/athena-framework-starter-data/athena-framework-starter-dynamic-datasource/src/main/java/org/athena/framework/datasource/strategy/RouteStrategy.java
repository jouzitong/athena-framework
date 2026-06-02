package org.athena.framework.datasource.strategy;

import org.athena.framework.datasource.routing.RouteRequest;

public interface RouteStrategy {

    String name();

    String determine(RouteRequest request);
}
