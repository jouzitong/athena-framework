package org.athena.framework.cloud.openfeign;

import feign.Logger;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * OpenFeign starter 配置项。
 */
@Data
@ConfigurationProperties(prefix = "athena.cloud.openfeign")
public class AthenaOpenFeignProperties {

    private boolean enabled = true;

    private String[] basePackages = new String[]{"org.athena"};

    private int connectTimeoutMillis = 3000;

    private int readTimeoutMillis = 5000;

    private Logger.Level loggerLevel = Logger.Level.BASIC;

    private String applicationNameHeader = "X-App-Name";
}
