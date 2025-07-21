package org.athena.framework.data.jdbc;

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
public class JdbcConfiguration {

    @Bean
    @ConditionalOnProperty(name = "lib.jdbc.type", havingValue = "MYSQL")
    public IDdlCreateService mysqlDdlCreateService() {
        return new MysqlDdlCreateService();
    }

    @Bean
    @ConditionalOnProperty(name = "lib.jdbc.type", havingValue = "POSTGRESQL")
    public IDdlCreateService postgresqlDdlCreateService() {
        return new PgsqlDdlCreateService();
    }

}
