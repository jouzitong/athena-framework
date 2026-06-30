package org.athena.framework.test.storage.mybatis.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;

/**
 * 测试模块 MyBatis 存储自动装配。
 */
@AutoConfiguration
@MapperScan("org.athena.framework.test.storage.mybatis.mapper")
public class TestMybatisStorageAutoConfiguration {
}
