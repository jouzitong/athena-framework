package org.athena.framework.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author liao
 */
@AutoConfiguration
@ComponentScan(basePackages = "org.athena.framework.web")
@Slf4j
public class WebAutoConfig {

    public WebAutoConfig() {
        LOGGER.info("web 自动配置开始加载...");
    }

//    public static void main(String[] args) {
//        String msg = ErrorCodeUtils.getMsg(5);
//        System.out.println(msg);
//    }

}
