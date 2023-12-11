package org.authena.mybatis;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhouzhitong
 * @since 2023-12-11
 **/
@Configuration
@AutoConfiguration
@ComponentScan({"com.zhouzhitong.lib.mapper"})
public class MybatisAutoConfig {
}
