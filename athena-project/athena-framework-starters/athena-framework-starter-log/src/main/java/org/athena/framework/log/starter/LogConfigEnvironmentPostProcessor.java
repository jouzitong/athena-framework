package org.athena.framework.log.starter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.StringUtils;

/**
 * Sets a default {@code logging.config} when the application has not configured one.
 * <p>
 * This enables "dependency-only" usage: once {@code athena-framework-starter-log} is on the classpath,
 * it provides a reasonable default Logback configuration without requiring any additional code.
 * <p>
 * If users explicitly set {@code logging.config}, this post-processor does nothing.
 */
public final class LogConfigEnvironmentPostProcessor implements EnvironmentPostProcessor, PriorityOrdered {

    static final String ENABLED_KEY = "athena.log.starter.enabled";
    static final String DEFAULT_CONFIG_KEY = "athena.log.starter.default-config";
    static final String LOGGING_CONFIG_KEY = "logging.config";

    static final String DEFAULT_CONFIG_LOCATION = "classpath:logback-spring.xml";
    static final String DEFAULT_EXTERNAL_CONFIG_LOCATION = "file:./config/logback-spring.xml";
    static final String DEFAULT_EXTERNAL_CONFIG_RELATIVE_PATH = "config/logback-spring.xml";
    static final String PROPERTY_SOURCE_NAME = "athenaLogDefaultLoggingConfig";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, org.springframework.boot.SpringApplication application) {
        if (!isEnabled(environment)) {
            return;
        }

        String existing = environment.getProperty(LOGGING_CONFIG_KEY);
        if (StringUtils.hasText(existing)) {
            return;
        }

        String configLocation = environment.getProperty(DEFAULT_CONFIG_KEY);
        if (!StringUtils.hasText(configLocation)) {
            configLocation = resolveDefaultConfigLocation();
        }
        if (!StringUtils.hasText(configLocation)) {
            configLocation = DEFAULT_CONFIG_LOCATION;
        }

        Map<String, Object> props = new HashMap<>();
        props.put(LOGGING_CONFIG_KEY, configLocation);

        environment.getPropertySources().addFirst(new MapPropertySource(PROPERTY_SOURCE_NAME, props));
    }

    private String resolveDefaultConfigLocation() {
        if (exists(DEFAULT_EXTERNAL_CONFIG_RELATIVE_PATH)) {
            return DEFAULT_EXTERNAL_CONFIG_LOCATION;
        }
        if (exists("logback-spring.xml")) {
            return "file:./logback-spring.xml";
        }
        return DEFAULT_CONFIG_LOCATION;
    }

    private boolean exists(String relativePath) {
        Path path = Paths.get(relativePath);
        return Files.isRegularFile(path);
    }

    private boolean isEnabled(ConfigurableEnvironment environment) {
        String enabled = environment.getProperty(ENABLED_KEY);
        return !StringUtils.hasText(enabled) || Boolean.parseBoolean(enabled);
    }

    /**
     * Runs early enough to affect logging system initialization.
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
