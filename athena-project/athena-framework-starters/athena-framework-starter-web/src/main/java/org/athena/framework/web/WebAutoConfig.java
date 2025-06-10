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
//        try {
//            String msg = ErrorCodeUtils.getMsg(1);
//            System.out.println(msg);
//            Properties properties = PropertiesUtils.loadAllProperties("ErrorCode-zh.properties");
//            System.out.println(properties.getProperty("1"));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

}
