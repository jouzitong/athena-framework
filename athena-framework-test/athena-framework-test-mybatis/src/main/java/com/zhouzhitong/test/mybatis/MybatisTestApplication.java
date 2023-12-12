package com.zhouzhitong.test.mybatis;

import org.arthena.common.CommonAutoConfiguration;
import org.authena.mybatis.MybatisAutoConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * @author zhouzhitong
 * @since 2023-12-11
 **/
@SpringBootApplication
@Import({CommonAutoConfiguration.class, MybatisAutoConfig.class})
@MapperScan("com.zhouzhitong.test.mybatis.mapper")
public class MybatisTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(MybatisTestApplication.class, args);
    }

}
