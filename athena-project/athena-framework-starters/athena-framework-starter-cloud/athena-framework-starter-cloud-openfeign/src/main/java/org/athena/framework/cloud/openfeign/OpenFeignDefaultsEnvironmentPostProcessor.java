package org.athena.framework.cloud.openfeign;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.StringUtils;

/**
 * 提供 OpenFeign 的通用默认值。
 */
public final class OpenFeignDefaultsEnvironmentPostProcessor implements EnvironmentPostProcessor, PriorityOrdered {

    static final String ENABLED_KEY = "athena.cloud.openfeign.enabled";
    static final String PROPERTY_SOURCE_NAME = "athenaOpenFeignDefaults";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if (!isEnabled(environment)) {
            return;
        }

        Map<String, Object> props = new HashMap<>();
        putIfMissing(environment, props, "athena.cloud.openfeign.base-packages", "org.athena");
        putIfMissing(environment, props, "spring.cloud.openfeign.client.config.default.connectTimeout", "3000");
        putIfMissing(environment, props, "spring.cloud.openfeign.client.config.default.readTimeout", "5000");
        putIfMissing(environment, props, "spring.cloud.openfeign.client.config.default.loggerLevel", "basic");
        putIfMissing(environment, props, "spring.cloud.openfeign.client.config.default.micrometer.enabled", "true");
        putIfMissing(environment, props, "spring.cloud.openfeign.micrometer.enabled", "true");
        putIfMissing(environment, props, "spring.cloud.openfeign.compression.request.enabled", "true");
        putIfMissing(environment, props, "spring.cloud.openfeign.compression.response.enabled", "true");

        if (!props.isEmpty()) {
            environment.getPropertySources().addFirst(new MapPropertySource(PROPERTY_SOURCE_NAME, props));
        }
    }

    private void putIfMissing(ConfigurableEnvironment environment, Map<String, Object> props, String key, String defaultValue) {
        if (!StringUtils.hasText(environment.getProperty(key))) {
            props.put(key, defaultValue);
        }
    }

    private boolean isEnabled(ConfigurableEnvironment environment) {
        String enabled = environment.getProperty(ENABLED_KEY);
        return !StringUtils.hasText(enabled) || Boolean.parseBoolean(enabled);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
