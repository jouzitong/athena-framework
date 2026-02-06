package com.zzt.starter.test.ws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author zhouzhitong
 * @since 2025/5/23
 **/
@SpringBootApplication(scanBasePackages = {"org.athena.framework"})
public class WsTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(WsTestApplication.class, args);
    }

}
