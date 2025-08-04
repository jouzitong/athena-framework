package org.arthena.framework.common;

import lombok.extern.slf4j.Slf4j;
import org.arthena.framework.common.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author zhouzhitong
 * @since 2023-12-12
 **/
@Slf4j
@ComponentScan({"org.arthena.framework"})
public class CommonAutoConfig {

    private static final Logger log = LoggerFactory.getLogger(CommonAutoConfig.class);

    public CommonAutoConfig() {
        LOGGER.info("CommonAutoConfig init");
        LOGGER.info("project work dir: {}", FileUtils.getSystemWorkDir());
    }

}
