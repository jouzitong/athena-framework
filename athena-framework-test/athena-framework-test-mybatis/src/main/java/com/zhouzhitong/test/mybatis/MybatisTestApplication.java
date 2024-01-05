package com.zhouzhitong.test.mybatis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author zhouzhitong
 * @since 2023-12-11
 **/
@SpringBootApplication
//@ComponentScan({"org.athena"})
@MapperScan("com.zhouzhitong.test.mybatis.mapper")
public class MybatisTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(MybatisTestApplication.class, args);
    }

}
