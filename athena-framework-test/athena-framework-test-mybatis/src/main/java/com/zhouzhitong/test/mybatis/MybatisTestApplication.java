package com.zhouzhitong.test.mybatis;

import org.arthena.common.CommonAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * @author zhouzhitong
 * @since 2023-12-11
 **/
@SpringBootApplication
//@Import({MybatisAutoConfig.class, CommonAutoConfiguration.class})
@Import({CommonAutoConfiguration.class})
public class MybatisTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(MybatisTestApplication.class, args);
    }

}
