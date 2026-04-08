package org.athena.framework.security.user.jpa.config;

import jakarta.persistence.EntityManager;
import org.athena.framework.security.api.spi.CredentialVerifier;
import org.athena.framework.security.api.spi.SecurityUserRepository;
import org.athena.framework.security.auth.core.config.SecurityAuthCoreAutoConfiguration;
import org.athena.framework.security.starter.config.SecurityAutoConfiguration;
import org.athena.framework.security.starter.marker.SecurityCoreMarker;
import org.athena.framework.security.user.jpa.repository.SecUserCredentialJpaRepository;
import org.athena.framework.security.user.jpa.repository.SecUserJpaRepository;
import org.athena.framework.security.user.jpa.service.JpaCredentialVerifier;
import org.athena.framework.security.user.jpa.service.JpaSecurityUserRepository;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 基于 JPA 的用户模块自动配置。
 * 注册密码编码器、用户仓储适配器和凭据校验器，实现数据库驱动的认证数据接入。
 */
@AutoConfiguration
@AutoConfigureAfter(SecurityAutoConfiguration.class)
@AutoConfigureBefore(SecurityAuthCoreAutoConfiguration.class)
@ConditionalOnClass(EntityManager.class)
@ConditionalOnBean(SecurityCoreMarker.class)
@ConditionalOnProperty(prefix = "athena.security.user.jpa", name = "enabled", havingValue = "true")
@EnableJpaRepositories(basePackageClasses = {SecUserJpaRepository.class, SecUserCredentialJpaRepository.class})
@EntityScan(basePackages = "org.athena.framework.security.user.jpa.entity")
public class SecurityUserJpaAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @ConditionalOnMissingBean(SecurityUserRepository.class)
    public SecurityUserRepository securityUserRepository(SecUserJpaRepository secUserJpaRepository,
                                                         SecUserCredentialJpaRepository credentialJpaRepository) {
        return new JpaSecurityUserRepository(secUserJpaRepository, credentialJpaRepository);
    }

    @Bean
    @ConditionalOnMissingBean(CredentialVerifier.class)
    public CredentialVerifier credentialVerifier(PasswordEncoder passwordEncoder) {
        return new JpaCredentialVerifier(passwordEncoder);
    }
}
