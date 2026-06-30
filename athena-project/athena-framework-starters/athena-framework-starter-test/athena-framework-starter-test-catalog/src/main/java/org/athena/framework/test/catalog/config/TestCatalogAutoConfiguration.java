package org.athena.framework.test.catalog.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;

/**
 * 接口资产自动装配占位。
 *
 * 当前阶段先由 storage-mybatis 提供实体表与持久化基础设施，
 * 这里不再默认注册内存实现，避免误用临时存储。
 */
@AutoConfiguration
public class TestCatalogAutoConfiguration {
}
