package com.zhouzhitong.test.mybatis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

/**
 * @author zhouzhitong
 * @since 2023-12-11
 **/
@SpringBootApplication
@PropertySource("classpath:config/mybatis-config.properties")
@MapperScan("com.zhouzhitong.test.mybatis.mapper")
public class MybatisTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(MybatisTestApplication.class, args);
    }

}
