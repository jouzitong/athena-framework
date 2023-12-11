package com.zhouzhitong.test.mybatis;

import com.zhouzhitong.lib.mapper.MybatisAutoConfiguration;
import org.arthena.lib.common.CommonAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * @author zhouzhitong
 * @since 2023-12-11
 **/
@SpringBootApplication
@Import({MybatisAutoConfiguration.class, CommonAutoConfiguration.class})
public class MybatisTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(MybatisTestApplication.class, args);
    }

}
