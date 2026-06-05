package org.athena.framework.cloud.seata;

import com.alibaba.druid.pool.DruidDataSource;
import io.seata.rm.datasource.DataSourceProxy;
import io.seata.rm.datasource.xa.DataSourceProxyXA;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.athena.framework.data.mybatis.handler.DefaultEnumTypeHandler;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

/**
 * Seata + MyBatis 核心自动配置。
 */
@Configuration
@Slf4j
public class CloudSeataMybatisSeataConfiguration {

    private final CloudSeataProperties properties;

    @Value("${mybatis.mapperLocations}")
    private String mapperLocations;

    @Value("${mybatis.type-aliases-package}")
    private String typeAliasesPackage;

    public CloudSeataMybatisSeataConfiguration(CloudSeataProperties properties) {
        this.properties = properties;
        LOGGER.info("{} 加载中...", this.getClass().getSimpleName());
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.druid")
    public DataSource druidDataSource() {
        LOGGER.debug("正在初始化数据源 ${spring.datasource.druid}, 请确保配置文件中已经配置了数据源相关属性");
        return new DruidDataSource();
    }

    @Primary
    @Bean("dataSource")
    public DataSource dataSourceProxy() {
        LOGGER.debug("正在初始化数据源代理, 代理模式: {}", properties.getDataSourceProxyMode());
        if ("XA".equalsIgnoreCase(properties.getDataSourceProxyMode())) {
            return new DataSourceProxyXA(druidDataSource());
        }
        return new DataSourceProxy(druidDataSource());
    }

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactoryBean() throws Exception {
        LOGGER.debug("正在初始化SqlSessionFactory");
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSourceProxy());
        sqlSessionFactoryBean.setDefaultEnumTypeHandler(DefaultEnumTypeHandler.class);
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(mapperLocations));
        sqlSessionFactoryBean.setTypeAliasesPackage(typeAliasesPackage);
        sqlSessionFactoryBean.setTransactionFactory(new SpringManagedTransactionFactory());
        return sqlSessionFactoryBean.getObject();
    }
}
