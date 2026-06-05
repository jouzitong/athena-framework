package org.athena.framework.cloud.seata;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Cloud Seata 配置项。
 */
@Data
@ConfigurationProperties(prefix = "athena.cloud.seata")
public class CloudSeataProperties {

    private boolean enabled = true;

    /**
     * 数据源代理模式，支持 AT / XA。
     */
    private String dataSourceProxyMode = "AT";
}
