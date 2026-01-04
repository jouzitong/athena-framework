package org.athena.framework.data.jdbc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author zhouzhitong
 * @since 2025/7/6
 **/
@Configuration
@Slf4j
@ComponentScan("org.athena.framework.data.jdbc")
@PropertySource("classpath:jdbc.properties")
public class JdbcConfiguration {

}
