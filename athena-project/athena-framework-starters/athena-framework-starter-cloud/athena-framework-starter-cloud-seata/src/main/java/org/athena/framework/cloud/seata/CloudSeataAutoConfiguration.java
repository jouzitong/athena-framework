package org.athena.framework.cloud.seata;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.apache.ibatis.session.SqlSessionFactory;

/**
 * Cloud Seata starter 入口配置。
 * 复用基础 seata starter 的数据源代理能力，作为云原生模块的聚合入口。
 */
@AutoConfiguration
@Slf4j
@ConditionalOnClass(SqlSessionFactory.class)
@ConditionalOnProperty(prefix = "athena.cloud.seata", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(CloudSeataProperties.class)
@Import(CloudSeataMybatisSeataConfiguration.class)
public class CloudSeataAutoConfiguration {

    public CloudSeataAutoConfiguration() {
        LOGGER.info("Cloud Seata 自动化配置加载中...");
    }
}
