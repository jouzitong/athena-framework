package org.athena.framework.seata.config;

import com.alibaba.druid.pool.DruidDataSource;
import io.seata.rm.datasource.DataSourceProxy;
import io.seata.rm.datasource.xa.DataSourceProxyXA;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.athena.framework.data.mybatis.handler.DefaultEnumTypeHandler;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

/**
 * 需要用到分布式事务的微服务都需要使用seata DataSourceProxy代理自己的数据源
 */
@Configuration
@Slf4j
public class MybatisSeataConfig {

    @Value("${seata.data-source-proxy-mode:AT}")
    private String seataMode;

    @Value("${mybatis.mapperLocations}")
    private String mapperLocations;

    @Value("${mybatis.type-aliases-package}")
    private String typeAliasesPackage;

    public MybatisSeataConfig() {
        LOGGER.info("{} 加载中...", this.getClass().getSimpleName());
    }

    /**
     * 从配置文件获取属性构造datasource，注意前缀，这里用的是druid，根据自己情况配置,
     * 原生datasource前缀取"spring.datasource"
     *
     * @return
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.druid")
    public DataSource druidDataSource() {
        LOGGER.debug("正在初始化数据源 ${spring.datasource.druid}, 请确保配置文件中已经配置了数据源相关属性");
        return new DruidDataSource();
    }

    /**
     * 构造datasource代理对象，替换原来的datasource
     *
     * @return DataSource 数据源代理对象
     */
    @Primary
    @Bean("dataSource")
    public DataSource dataSourceProxy() {
        LOGGER.debug("正在初始化数据源代理, 代理模式: {}", seataMode);
        if ("XA".equalsIgnoreCase(seataMode)) {
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
