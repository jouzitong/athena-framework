package org.athena.framework.es;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhouzhitong
 * @since 2025/5/23
 **/
@Configuration
@Slf4j
@ComponentScan("org.athena.framework.es")
@AutoConfiguration
public class EsConfig {
}
