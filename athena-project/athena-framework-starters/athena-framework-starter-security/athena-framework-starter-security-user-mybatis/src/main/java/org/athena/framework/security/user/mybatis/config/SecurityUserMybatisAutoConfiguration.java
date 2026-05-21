package org.athena.framework.security.user.mybatis.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.athena.framework.security.api.spi.CredentialVerifier;
import org.athena.framework.security.api.spi.SecurityUserRepository;
import org.athena.framework.security.auth.core.config.SecurityAuthCoreAutoConfiguration;
import org.athena.framework.security.starter.config.SecurityAutoConfiguration;
import org.athena.framework.security.starter.marker.SecurityCoreMarker;
import org.athena.framework.security.user.mybatis.repository.SecUserCredentialMybatisMapper;
import org.athena.framework.security.user.mybatis.repository.SecUserMybatisMapper;
import org.athena.framework.security.user.mybatis.service.MybatisCredentialVerifier;
import org.athena.framework.security.user.mybatis.service.MybatisSecurityUserRepository;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@AutoConfiguration
@AutoConfigureAfter(SecurityAutoConfiguration.class)
@AutoConfigureBefore(SecurityAuthCoreAutoConfiguration.class)
@ConditionalOnClass(SqlSessionFactory.class)
@ConditionalOnBean(SecurityCoreMarker.class)
@ConditionalOnProperty(prefix = "athena.security.user.mybatis", name = "enabled", havingValue = "true")
@MapperScan(basePackageClasses = {SecUserMybatisMapper.class, SecUserCredentialMybatisMapper.class})
public class SecurityUserMybatisAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @ConditionalOnMissingBean(SecurityUserRepository.class)
    public SecurityUserRepository securityUserRepository(SecUserMybatisMapper userMapper,
                                                         SecUserCredentialMybatisMapper credentialMapper) {
        return new MybatisSecurityUserRepository(userMapper, credentialMapper);
    }

    @Bean
    @ConditionalOnMissingBean(CredentialVerifier.class)
    public CredentialVerifier credentialVerifier(PasswordEncoder passwordEncoder) {
        return new MybatisCredentialVerifier(passwordEncoder);
    }
}
