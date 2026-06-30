package org.athena.framework.test.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 测试模块配置。
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "athena.test")
public class AthenaTestProperties {

    private boolean enabled = true;

    private boolean failFast = true;

    private long defaultStepTimeoutMs = 30000L;
}
