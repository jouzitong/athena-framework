package org.athena.framework.cloud.seata;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;

/**
 * Cloud Seata starter 入口配置。
 * 复用基础 seata starter 的数据源代理能力，作为云原生模块的聚合入口。
 */
@AutoConfiguration
@Slf4j
public class CloudSeataAutoConfiguration {

    public CloudSeataAutoConfiguration() {
        LOGGER.info("Cloud Seata 自动化配置加载中...");
    }
}
