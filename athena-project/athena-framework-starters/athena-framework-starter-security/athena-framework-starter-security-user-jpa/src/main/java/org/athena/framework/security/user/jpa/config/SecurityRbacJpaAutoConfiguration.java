package org.athena.framework.security.user.jpa.config;

import jakarta.persistence.EntityManager;
import org.athena.framework.security.api.spi.AuthorizationProvider;
import org.athena.framework.security.api.spi.RolePermissionResolver;
import org.athena.framework.security.api.spi.RoleProvider;
import org.athena.framework.security.api.spi.UserContextEnricher;
import org.athena.framework.security.starter.marker.SecurityCoreMarker;
import org.athena.framework.security.user.jpa.repository.SecPermissionJpaRepository;
import org.athena.framework.security.user.jpa.repository.SecRoleJpaRepository;
import org.athena.framework.security.user.jpa.repository.SecRolePermissionJpaRepository;
import org.athena.framework.security.user.jpa.repository.SecUserRoleJpaRepository;
import org.athena.framework.security.user.jpa.service.rbac.JpaRbacAuthorizationProvider;
import org.athena.framework.security.user.jpa.service.rbac.JpaRolePermissionResolver;
import org.athena.framework.security.user.jpa.service.rbac.JpaRoleProvider;
import org.athena.framework.security.user.jpa.service.rbac.RbacUserContextEnricher;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@AutoConfigureAfter(SecurityUserJpaAutoConfiguration.class)
@AutoConfigureBefore(name = "org.athena.framework.security.authorization.config.SecurityAuthorizationAutoConfiguration")
@ConditionalOnClass(EntityManager.class)
@ConditionalOnBean(SecurityCoreMarker.class)
@ConditionalOnProperty(prefix = "athena.security.rbac", name = "enabled", havingValue = "true")
public class SecurityRbacJpaAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(RoleProvider.class)
    public RoleProvider roleProvider(SecUserRoleJpaRepository userRoleRepository,
                                     SecRoleJpaRepository roleRepository) {
        return new JpaRoleProvider(userRoleRepository, roleRepository);
    }

    @Bean
    @ConditionalOnMissingBean(RolePermissionResolver.class)
    public RolePermissionResolver rolePermissionResolver(SecRolePermissionJpaRepository rolePermissionRepository,
                                                         SecPermissionJpaRepository permissionRepository) {
        return new JpaRolePermissionResolver(rolePermissionRepository, permissionRepository);
    }

    @Bean
    @ConditionalOnMissingBean(AuthorizationProvider.class)
    public AuthorizationProvider authorizationProvider(RoleProvider roleProvider,
                                                       RolePermissionResolver rolePermissionResolver) {
        return new JpaRbacAuthorizationProvider(roleProvider, rolePermissionResolver);
    }

    @Bean
    @ConditionalOnMissingBean(name = "rbacUserContextEnricher")
    public UserContextEnricher rbacUserContextEnricher(RoleProvider roleProvider,
                                                       RolePermissionResolver rolePermissionResolver) {
        return new RbacUserContextEnricher(roleProvider, rolePermissionResolver);
    }
}
