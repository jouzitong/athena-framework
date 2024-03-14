package org.athena.framework.mybatis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

/**
 * @author zhouzhitong
 * @since 2023-12-11
 **/
@AutoConfiguration
@ComponentScan("org.athena.framework.mybatis")
@PropertySource("classpath:config/mybatis-config.properties")
@Slf4j
public class MybatisAutoConfig {

    public MybatisAutoConfig() {
        LOGGER.info("Mybatis 自动化配置加载开始加载...");
    }
}
