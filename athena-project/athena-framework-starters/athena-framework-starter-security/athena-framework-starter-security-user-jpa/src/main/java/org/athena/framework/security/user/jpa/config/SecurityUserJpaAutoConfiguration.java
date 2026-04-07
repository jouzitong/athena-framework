package org.athena.framework.security.user.jpa.config;

import jakarta.persistence.EntityManager;
import org.athena.framework.security.api.spi.CredentialVerifier;
import org.athena.framework.security.api.spi.SecurityUserRepository;
import org.athena.framework.security.starter.marker.SecurityCoreMarker;
import org.athena.framework.security.user.jpa.repository.SecUserCredentialJpaRepository;
import org.athena.framework.security.user.jpa.repository.SecUserJpaRepository;
import org.athena.framework.security.user.jpa.service.JpaCredentialVerifier;
import org.athena.framework.security.user.jpa.service.JpaSecurityUserRepository;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@AutoConfiguration
@ConditionalOnClass(EntityManager.class)
@ConditionalOnBean(SecurityCoreMarker.class)
@ConditionalOnProperty(prefix = "athena.security.user.jpa", name = "enabled", havingValue = "true")
@EnableJpaRepositories(basePackageClasses = {SecUserJpaRepository.class, SecUserCredentialJpaRepository.class})
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
