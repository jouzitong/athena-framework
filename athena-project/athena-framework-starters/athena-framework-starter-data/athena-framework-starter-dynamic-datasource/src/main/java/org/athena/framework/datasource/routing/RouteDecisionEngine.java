package org.athena.framework.datasource.routing;

import org.athena.framework.datasource.strategy.RouteStrategy;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RouteDecisionEngine {

    private final List<String> strategyOrder;

    private final Map<String, RouteStrategy> strategyMap;

    public RouteDecisionEngine(List<String> strategyOrder, List<RouteStrategy> strategies) {
        this.strategyOrder = strategyOrder;
        this.strategyMap = strategies.stream().collect(Collectors.toMap(RouteStrategy::name, item -> item));
    }

    public String determine(RouteRequest request) {
        for (String strategyName : strategyOrder) {
            RouteStrategy strategy = strategyMap.get(strategyName);
            if (strategy == null) {
                continue;
            }
            String key = strategy.determine(request);
            if (key != null && !key.isBlank()) {
                return key;
            }
        }
        return null;
    }
}
