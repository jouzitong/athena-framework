package org.athena.framework.security.user.mybatis.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.athena.framework.security.api.spi.AuthorizationProvider;
import org.athena.framework.security.api.spi.RolePermissionResolver;
import org.athena.framework.security.api.spi.RoleProvider;
import org.athena.framework.security.api.spi.UserContextEnricher;
import org.athena.framework.security.starter.marker.SecurityCoreMarker;
import org.athena.framework.security.user.mybatis.repository.SecPermissionMybatisMapper;
import org.athena.framework.security.user.mybatis.repository.SecRoleMybatisMapper;
import org.athena.framework.security.user.mybatis.repository.SecRolePermissionMybatisMapper;
import org.athena.framework.security.user.mybatis.repository.SecUserRoleMybatisMapper;
import org.athena.framework.security.user.mybatis.service.rbac.MybatisRbacAuthorizationProvider;
import org.athena.framework.security.user.mybatis.service.rbac.MybatisRolePermissionResolver;
import org.athena.framework.security.user.mybatis.service.rbac.MybatisRoleProvider;
import org.athena.framework.security.user.mybatis.service.rbac.RbacUserContextEnricher;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@AutoConfigureAfter(SecurityUserMybatisAutoConfiguration.class)
@AutoConfigureBefore(name = "org.athena.framework.security.authorization.config.SecurityAuthorizationAutoConfiguration")
@ConditionalOnClass(SqlSessionFactory.class)
@ConditionalOnBean(SecurityCoreMarker.class)
@ConditionalOnProperty(prefix = "athena.security.rbac", name = "enabled", havingValue = "true")
public class SecurityRbacMybatisAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(RoleProvider.class)
    public RoleProvider roleProvider(SecUserRoleMybatisMapper userRoleMapper,
                                     SecRoleMybatisMapper roleMapper) {
        return new MybatisRoleProvider(userRoleMapper, roleMapper);
    }

    @Bean
    @ConditionalOnMissingBean(RolePermissionResolver.class)
    public RolePermissionResolver rolePermissionResolver(SecRolePermissionMybatisMapper rolePermissionMapper,
                                                         SecPermissionMybatisMapper permissionMapper) {
        return new MybatisRolePermissionResolver(rolePermissionMapper, permissionMapper);
    }

    @Bean
    @ConditionalOnMissingBean(AuthorizationProvider.class)
    public AuthorizationProvider authorizationProvider(RoleProvider roleProvider,
                                                       RolePermissionResolver rolePermissionResolver) {
        return new MybatisRbacAuthorizationProvider(roleProvider, rolePermissionResolver);
    }

    @Bean
    @ConditionalOnMissingBean(name = "rbacUserContextEnricher")
    public UserContextEnricher rbacUserContextEnricher(RoleProvider roleProvider,
                                                        RolePermissionResolver rolePermissionResolver) {
        return new RbacUserContextEnricher(roleProvider, rolePermissionResolver);
    }
}
