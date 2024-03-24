package com.zhouzhitong.test.mybatis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author zhouzhitong
 * @since 2023-12-11
 **/
@SpringBootApplication
@MapperScan("com.zhouzhitong.test.mybatis.mapper")
@EnableDiscoveryClient
public class MybatisTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(MybatisTestApplication.class, args);
    }

}
