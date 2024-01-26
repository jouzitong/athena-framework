package org.athena.framework.mybatis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhouzhitong
 * @since 2023-12-11
 **/
@Configuration
@ComponentScan("org.athena.framework.mybatis")
@Slf4j
public class MybatisAutoConfig {

    public MybatisAutoConfig() {
        LOGGER.info("MybatisAutoConfig init");
    }
}
