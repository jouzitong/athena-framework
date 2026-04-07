package org.athena.framework.security.authorization.config;

import org.athena.framework.security.api.spi.AuthorizationProvider;
import org.athena.framework.security.api.spi.PermissionEvaluator;
import org.athena.framework.security.authorization.aop.PermissionAuthorizationAspect;
import org.athena.framework.security.authorization.service.DefaultPermissionEvaluator;
import org.athena.framework.security.starter.marker.SecurityCoreMarker;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnBean(SecurityCoreMarker.class)
@ConditionalOnProperty(prefix = "athena.security.authorization", name = "enabled", havingValue = "true")
public class SecurityAuthorizationAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(AuthorizationProvider.class)
    public AuthorizationProvider authorizationProvider() {
        return new DefaultPermissionEvaluator.EmptyAuthorizationProvider();
    }

    @Bean
    @ConditionalOnMissingBean(PermissionEvaluator.class)
    public PermissionEvaluator permissionEvaluator(AuthorizationProvider authorizationProvider) {
        return new DefaultPermissionEvaluator(authorizationProvider);
    }

    @Bean
    @ConditionalOnMissingBean
    public PermissionAuthorizationAspect permissionAuthorizationAspect(PermissionEvaluator permissionEvaluator) {
        return new PermissionAuthorizationAspect(permissionEvaluator);
    }
}
