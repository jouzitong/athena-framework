package org.athena.framework.security.auth.core.config;

import jakarta.servlet.Filter;
import org.arthena.framework.common.context.SystemContext;
import org.arthena.framework.common.service.IUserContextService;
import org.athena.framework.security.api.spi.Authenticator;
import org.athena.framework.security.api.spi.CredentialVerifier;
import org.athena.framework.security.api.spi.IdentityProvider;
import org.athena.framework.security.api.spi.SecurityUserRepository;
import org.athena.framework.security.api.spi.TokenManager;
import org.athena.framework.security.api.spi.UserContextEnricher;
import org.athena.framework.security.auth.core.context.SecurityContextHolder;
import org.athena.framework.security.auth.core.extractor.CredentialExtractor;
import org.athena.framework.security.auth.core.extractor.HeaderTokenCredentialExtractor;
import org.athena.framework.security.auth.core.service.DefaultAuthenticator;
import org.athena.framework.security.auth.core.service.DefaultIdentityProvider;
import org.athena.framework.security.auth.core.service.DefaultSecurityUserRepository;
import org.athena.framework.security.auth.core.service.NoopUserContextEnricher;
import org.athena.framework.security.auth.core.service.PlainCredentialVerifier;
import org.athena.framework.security.auth.core.service.SecurityAuthenticationService;
import org.athena.framework.security.auth.core.token.LocalTokenManager;
import org.athena.framework.security.auth.core.web.SecurityAuthController;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.List;

/**
 * 认证核心自动配置。
 * 负责装配认证链路中的默认实现（用户仓储、认证器、令牌管理、过滤器与控制器）。
 */
@AutoConfiguration
@AutoConfigureAfter(name = "org.athena.framework.security.user.jpa.config.SecurityUserJpaAutoConfiguration")
@ConditionalOnProperty(prefix = "athena.security.auth", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(SecurityAuthProperties.class)
public class SecurityAuthCoreAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SecurityUserRepository securityUserRepository() {
        return new DefaultSecurityUserRepository();
    }

    @Bean
    @ConditionalOnMissingBean
    public CredentialVerifier credentialVerifier() {
        return new PlainCredentialVerifier();
    }

    @Bean
    @ConditionalOnMissingBean
    public IdentityProvider identityProvider(SecurityUserRepository securityUserRepository) {
        return new DefaultIdentityProvider(securityUserRepository);
    }

    @Bean
    @ConditionalOnMissingBean
    public Authenticator authenticator(SecurityUserRepository securityUserRepository,
                                       CredentialVerifier credentialVerifier) {
        return new DefaultAuthenticator(securityUserRepository, credentialVerifier);
    }

    @Bean
    @ConditionalOnMissingBean
    public UserContextEnricher userContextEnricher() {
        return new NoopUserContextEnricher();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "athena.security.token", name = "type", havingValue = "local", matchIfMissing = true)
    public TokenManager tokenManager() {
        return new LocalTokenManager();
    }

    @Bean
    @ConditionalOnMissingBean
    public CredentialExtractor credentialExtractor(SecurityAuthProperties properties) {
        return new HeaderTokenCredentialExtractor(properties.getTokenHeader(), properties.getTokenPrefix());
    }

    @Bean
    @ConditionalOnMissingBean
    public SecurityAuthenticationService securityAuthenticationService(Authenticator authenticator,
                                                                       TokenManager tokenManager,
                                                                       List<UserContextEnricher> enrichers,
                                                                       ApplicationEventPublisher eventPublisher) {
        return new SecurityAuthenticationService(authenticator, tokenManager, enrichers, eventPublisher);
    }

    @Bean
    @ConditionalOnMissingBean
    public SecurityAuthController securityAuthController(SecurityAuthenticationService securityAuthenticationService,
                                                         CredentialExtractor credentialExtractor) {
        return new SecurityAuthController(securityAuthenticationService, credentialExtractor);
    }

    @Bean
    @ConditionalOnClass(Filter.class)
    @ConditionalOnMissingBean(name = "securityContextFilterRegistrationBean")
    public FilterRegistrationBean<SecurityContextFilter> securityContextFilterRegistrationBean(CredentialExtractor credentialExtractor,
                                                                                                TokenManager tokenManager,
                                                                                                List<UserContextEnricher> enrichers,
                                                                                                SecurityAuthProperties properties) {
        FilterRegistrationBean<SecurityContextFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new SecurityContextFilter(credentialExtractor, tokenManager, enrichers, properties));
        bean.setOrder(-110);
        bean.addUrlPatterns("/*");
        return bean;
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean(name = "securityIUserContextService")
    public IUserContextService securityIUserContextService() {
        return new IUserContextService() {
            @Override
            public Long getUserId() {
                if (SecurityContextHolder.get() == null || SecurityContextHolder.get().subject() == null) {
                    return 0L;
                }
                try {
                    return Long.parseLong(SecurityContextHolder.get().subject().userId());
                } catch (Exception ex) {
                    return 0L;
                }
            }

            @Override
            public String getLocale() {
                return SystemContext.getLocale();
            }
        };
    }
}
