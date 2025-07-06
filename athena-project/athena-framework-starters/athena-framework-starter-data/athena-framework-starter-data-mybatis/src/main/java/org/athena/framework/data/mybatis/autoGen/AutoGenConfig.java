package org.athena.framework.data.mybatis.autoGen;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.athena.framework.data.mybatis.autoGen.mysql.MySqlDatabaseMapConfig;
import org.athena.framework.data.mybatis.autoGen.mysql.MysqlTableGenService;
import org.athena.framework.data.mybatis.autoGen.pgsql.PgSqlDatabaseMapConfig;
import org.athena.framework.data.mybatis.autoGen.pgsql.PgSqlTableGenService;
import org.athena.framework.data.mybatis.properties.DefaultMapperProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author zhouzhitong
 * @since 2024-03-24
 **/
@Configuration
public class AutoGenConfig {

    @Configuration
    @ConditionalOnProperty(prefix = "lib.mapper.auto-gen", name = "type", havingValue = "mysql")
    public static class MysqlAutoConfig {

        @Bean
        public DatabaseTypeMapConfig mySqlGenDdlConfig() {
            return new MySqlDatabaseMapConfig();
        }

        @Bean
        public MysqlTableGenService mysqlTableGenService(DefaultMapperProperties mapperProperties,
                                                         DataSource dataSource) {
            return new MysqlTableGenService(mapperProperties, mySqlGenDdlConfig(), dataSource);
        }

        /**
         * 配置MybatisPlus分页插件
         *
         * @return MybatisPlusInterceptor
         */
        @Bean
        public MybatisPlusInterceptor mybatisPlusInterceptor() {
            MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
            interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
            return interceptor;
        }



    }

    @Configuration
    @ConditionalOnProperty(prefix = "lib.mapper.auto-gen", name = "type", havingValue = "pgsql")
    public static class PgSqlAutoConfig {

        @Bean
        public DatabaseTypeMapConfig pgSqlGenDdlConfig() {
            return new PgSqlDatabaseMapConfig();
        }

        @Bean
        public PgSqlTableGenService pgSqlTableGenService(DefaultMapperProperties mapperProperties,
                                                         DataSource dataSource) {
            return new PgSqlTableGenService(mapperProperties, pgSqlGenDdlConfig(), dataSource);
        }

        /**
         * 配置MybatisPlus分页插件
         *
         * @return MybatisPlusInterceptor
         */
        @Bean
        public MybatisPlusInterceptor mybatisPlusInterceptor() {
            MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
            PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
            paginationInnerInterceptor.setOptimizeJoin(true);
            paginationInnerInterceptor.setDbType(DbType.POSTGRE_SQL);
            paginationInnerInterceptor.setOverflow(true);
            interceptor.addInnerInterceptor(paginationInnerInterceptor);
            OptimisticLockerInnerInterceptor optimisticLockerInnerInterceptor = new OptimisticLockerInnerInterceptor();
            interceptor.addInnerInterceptor(optimisticLockerInnerInterceptor);
            return interceptor;
        }

    }


}
