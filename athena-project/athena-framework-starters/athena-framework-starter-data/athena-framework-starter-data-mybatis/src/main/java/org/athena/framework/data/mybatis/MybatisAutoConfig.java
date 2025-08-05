package org.athena.framework.data.mybatis;

import lombok.extern.slf4j.Slf4j;
import org.athena.framework.data.mybatis.create.IDdlCreateService;
import org.athena.framework.data.mybatis.create.impl.MysqlDdlCreateService;
import org.athena.framework.data.mybatis.create.impl.PgsqlDdlCreateService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author zhouzhitong
 * @since 2023-12-11
 **/
@AutoConfiguration
@ComponentScan("org.athena.framework.data.mybatis")
@Slf4j
public class MybatisAutoConfig {

    public MybatisAutoConfig() {
        LOGGER.info("Mybatis 自动化配置加载开始加载...");
    }
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
