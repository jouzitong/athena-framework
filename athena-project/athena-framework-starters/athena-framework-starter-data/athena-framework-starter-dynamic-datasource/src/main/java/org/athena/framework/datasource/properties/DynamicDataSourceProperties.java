package org.athena.framework.datasource.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = "athena.datasource.dynamic")
public class DynamicDataSourceProperties {

    private boolean enabled = false;

    private boolean strict = false;

    private String primary = "master";

    private String strategyOrder = "operation,annotation,tenant,readwrite";

    private Map<String, DataSourceItem> datasources = new HashMap<>();

    private Map<String, String> tenants = new HashMap<>();

    private Map<String, GroupConfig> groups = new HashMap<>();

    private List<OperationRouteRule> operationRoutes = new ArrayList<>();

    @Data
    public static class DataSourceItem {

        private String url;

        private String username;

        private String password;

        private String driverClassName;
    }

    @Data
    public static class GroupConfig {

        private String master;

        private List<String> slaves = new ArrayList<>();
    }

    @Data
    public static class OperationRouteRule {

        private String id;

        private Integer priority = 100;

        private String service;

        private String mapper;

        private String method;

        private String tenant;

        private String target;
    }
}
