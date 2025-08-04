package org.athena.framework.data.jdbc;

import lombok.extern.slf4j.Slf4j;
import org.athena.framework.data.jdbc.create.IDdlCreateService;
import org.athena.framework.data.jdbc.create.impl.MysqlDdlCreateService;
import org.athena.framework.data.jdbc.create.impl.PgsqlDdlCreateService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhouzhitong
 * @since 2025/7/6
 **/
@Configuration
@Slf4j
public class JdbcConfiguration {

    @Bean
    @ConditionalOnProperty(name = "lib.jdbc.type", havingValue = "MYSQL")
    public IDdlCreateService mysqlDdlCreateService() {
        LOGGER.info("JdbcConfiguration.mysqlDdlCreateService");
        return new MysqlDdlCreateService();
    }

    @Bean
    @ConditionalOnProperty(name = "lib.jdbc.type", havingValue = "POSTGRESQL")
    public IDdlCreateService postgresqlDdlCreateService() {
        LOGGER.info("JdbcConfiguration.postgresqlDdlCreateService");
        return new PgsqlDdlCreateService();
    }

}
