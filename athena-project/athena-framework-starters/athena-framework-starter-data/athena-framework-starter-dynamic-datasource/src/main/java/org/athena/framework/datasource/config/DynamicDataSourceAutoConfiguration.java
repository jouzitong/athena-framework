package org.athena.framework.datasource.config;

import lombok.extern.slf4j.Slf4j;
import org.athena.framework.datasource.aop.DynamicDataSourceAspect;
import org.athena.framework.datasource.properties.DynamicDataSourceProperties;
import org.athena.framework.datasource.routing.DynamicRoutingDataSource;
import org.athena.framework.datasource.routing.RouteDecisionEngine;
import org.athena.framework.datasource.strategy.OperationRouteStrategy;
import org.athena.framework.datasource.strategy.ReadWriteRouteStrategy;
import org.athena.framework.datasource.strategy.RouteStrategy;
import org.athena.framework.datasource.strategy.TenantRouteStrategy;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@AutoConfiguration
@ConditionalOnClass(DataSource.class)
@ConditionalOnProperty(prefix = "athena.datasource.dynamic", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(DynamicDataSourceProperties.class)
public class DynamicDataSourceAutoConfiguration {

    @Bean
    @Primary
    @ConditionalOnMissingBean
    public DataSource dataSource(DynamicDataSourceProperties properties) {
        Map<String, DataSource> allDataSources = new LinkedHashMap<>();
        for (Map.Entry<String, DynamicDataSourceProperties.DataSourceItem> entry : properties.getDatasources().entrySet()) {
            DynamicDataSourceProperties.DataSourceItem item = entry.getValue();
            DataSourceBuilder<?> builder = DataSourceBuilder.create();
            builder.url(item.getUrl());
            builder.username(item.getUsername());
            builder.password(item.getPassword());
            if (StringUtils.hasText(item.getDriverClassName())) {
                builder.driverClassName(item.getDriverClassName());
            }
            allDataSources.put(entry.getKey(), builder.build());
        }

        if (allDataSources.isEmpty()) {
            throw new IllegalStateException("athena.datasource.dynamic.datasources cannot be empty");
        }

        DataSource primary = allDataSources.get(properties.getPrimary());
        if (primary == null) {
            throw new IllegalStateException("primary datasource not found: " + properties.getPrimary());
        }

        DynamicRoutingDataSource dynamic = new DynamicRoutingDataSource();
        dynamic.setDefaultTargetDataSource(primary);
        dynamic.setTargetDataSources(new LinkedHashMap<>(allDataSources));
        dynamic.afterPropertiesSet();
        LOGGER.info("dynamic datasource loaded, total={}, primary={}", allDataSources.size(), properties.getPrimary());
        return dynamic;
    }

    @Bean
    @ConditionalOnMissingBean
    public RouteDecisionEngine routeDecisionEngine(DynamicDataSourceProperties properties, List<RouteStrategy> strategies) {
        List<String> order = new ArrayList<>();
        for (String name : Arrays.asList(properties.getStrategyOrder().split(","))) {
            if (name != null && !name.isBlank()) {
                order.add(name.trim());
            }
        }
        if (order.isEmpty()) {
            order = Arrays.asList("operation", "annotation", "tenant", "readwrite");
        }
        return new RouteDecisionEngine(order, strategies);
    }

    @Bean
    @ConditionalOnMissingBean(name = "operationRouteStrategy")
    public RouteStrategy operationRouteStrategy(DynamicDataSourceProperties properties) {
        return new OperationRouteStrategy(properties);
    }

    @Bean
    @ConditionalOnMissingBean(name = "tenantRouteStrategy")
    public RouteStrategy tenantRouteStrategy(DynamicDataSourceProperties properties) {
        return new TenantRouteStrategy(properties);
    }

    @Bean
    @ConditionalOnMissingBean(name = "readWriteRouteStrategy")
    public RouteStrategy readWriteRouteStrategy(DynamicDataSourceProperties properties) {
        return new ReadWriteRouteStrategy(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public DynamicDataSourceAspect dynamicDataSourceAspect(RouteDecisionEngine routeDecisionEngine) {
        return new DynamicDataSourceAspect(routeDecisionEngine);
    }
}
