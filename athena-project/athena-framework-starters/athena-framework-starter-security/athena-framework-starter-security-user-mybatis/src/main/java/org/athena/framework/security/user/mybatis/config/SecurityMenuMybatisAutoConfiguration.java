package org.athena.framework.security.user.mybatis.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.athena.framework.security.api.spi.MenuProvider;
import org.athena.framework.security.starter.marker.SecurityCoreMarker;
import org.athena.framework.security.user.mybatis.repository.SecMenuMybatisMapper;
import org.athena.framework.security.user.mybatis.service.menu.MybatisMenuProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@AutoConfigureAfter(SecurityRbacMybatisAutoConfiguration.class)
@ConditionalOnClass(SqlSessionFactory.class)
@ConditionalOnBean(SecurityCoreMarker.class)
@ConditionalOnProperty(prefix = "athena.security.menu", name = "enabled", havingValue = "true")
public class SecurityMenuMybatisAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(MenuProvider.class)
    public MenuProvider menuProvider(SecMenuMybatisMapper menuMapper) {
        return new MybatisMenuProvider(menuMapper);
    }
}
