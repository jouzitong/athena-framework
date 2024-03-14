package org.athena.framework.seata;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author zhouzhitong
 * @since 2024-03-14
 **/
@AutoConfiguration
@Slf4j
@ComponentScan("org.athena.framework.seata")
public class SeataAutoConfig {

    public SeataAutoConfig() {
        LOGGER.info("Seata 自动化配置加载中...");
    }

}
