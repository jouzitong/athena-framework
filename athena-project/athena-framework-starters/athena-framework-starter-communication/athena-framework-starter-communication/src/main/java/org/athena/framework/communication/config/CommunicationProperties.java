package org.athena.framework.communication.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 通信公共配置。
 *
 * @author zhouzhitong
 * @since 2026/6/24
 */
@Data
@ConfigurationProperties(prefix = "athena.communication")
public class CommunicationProperties {

    private boolean enabled = true;

    private boolean allowDirectContent = true;
}
