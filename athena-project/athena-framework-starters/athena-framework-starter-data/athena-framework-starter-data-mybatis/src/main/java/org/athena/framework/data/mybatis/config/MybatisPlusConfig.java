package org.athena.framework.data.mybatis.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.athena.framework.data.mybatis.interceptor.DataOperationInterceptor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author zhouzhitong
 * @since 2023-12-12
 **/
@Configuration
public class MybatisPlusConfig {

    /**
     * 配置Druid数据源
     *
     * @return DruidDataSource
     */
//    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.druid")
    public DataSource druid() {
        return new DruidDataSource();
    }

    /**
     * 数据自动插入拦截器
     * 自动插入的数据包括：创建人、创建时间、修改人、修改时间
     */
    @Bean
    public DataOperationInterceptor dataOperationInterceptor() {
        return new DataOperationInterceptor();
    }


}
