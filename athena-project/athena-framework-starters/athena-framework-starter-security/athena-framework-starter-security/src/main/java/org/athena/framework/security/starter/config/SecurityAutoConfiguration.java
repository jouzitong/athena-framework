package org.athena.framework.security.starter.config;

import lombok.extern.slf4j.Slf4j;
import org.athena.framework.security.api.spi.TokenManager;
import org.athena.framework.security.auth.core.token.LocalTokenManager;
import org.athena.framework.security.starter.marker.SecurityCoreMarker;
import org.athena.framework.security.starter.properties.SecurityProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.ObjectProvider;

/**
 * 安全模块顶层自动配置。
 * 提供核心 marker，并在启动时校验 token 类型配置是否与实际模块一致。
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "athena.security", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(SecurityProperties.class)
@Slf4j
public class SecurityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SecurityCoreMarker securityCoreMarker() {
        return new SecurityCoreMarker() {
        };
    }

    @Bean
    @ConditionalOnBean(SecurityCoreMarker.class)
    @ConditionalOnMissingBean
    public SecurityTokenTypeValidator securityTokenTypeValidator(SecurityProperties properties, ObjectProvider<TokenManager> tokenManagerProvider) {
        return new SecurityTokenTypeValidator(properties, tokenManagerProvider);
    }

    @Bean
    @ConditionalOnBean(SecurityCoreMarker.class)
    @ConditionalOnMissingBean
    public SecurityUserDataSourceValidator securityUserDataSourceValidator(SecurityProperties properties) {
        return new SecurityUserDataSourceValidator(properties);
    }

    /**
     * token 类型校验器。
     * 在容器启动时检测 local/jwt/redis 配置组合是否合法。
     */
    public static class SecurityTokenTypeValidator {

        private final SecurityProperties properties;

        private final ObjectProvider<TokenManager> tokenManagerProvider;

        public SecurityTokenTypeValidator(SecurityProperties properties, ObjectProvider<TokenManager> tokenManagerProvider) {
            this.properties = properties;
            this.tokenManagerProvider = tokenManagerProvider;
            validate();
        }

        private void validate() {
            String tokenType = properties.getToken().getType();
            TokenManager tokenManager = tokenManagerProvider.getIfAvailable();
            if ("local".equalsIgnoreCase(tokenType)) {
                if (!(tokenManager instanceof LocalTokenManager)) {
                    throw new IllegalStateException("athena.security.token.type=local but local token module is not active");
                }
                return;
            }

            if ("jwt".equalsIgnoreCase(tokenType)) {
                if (!properties.getToken().getJwt().isEnabled()) {
                    throw new IllegalStateException("athena.security.token.type=jwt but athena.security.token.jwt.enabled=false");
                }
                if (tokenManager == null || tokenManager instanceof LocalTokenManager) {
                    throw new IllegalStateException("athena.security.token.type=jwt but JWT token module is not active");
                }
                return;
            }

            if ("redis".equalsIgnoreCase(tokenType)) {
                if (!properties.getToken().getRedis().isEnabled()) {
                    throw new IllegalStateException("athena.security.token.type=redis but athena.security.token.redis.enabled=false");
                }
                if (tokenManager == null || tokenManager instanceof LocalTokenManager) {
                    throw new IllegalStateException("athena.security.token.type=redis but redis token module is not active");
                }
                return;
            }

            throw new IllegalStateException("Unsupported token type: " + tokenType);
        }
    }

    /**
     * 用户数据源配置校验器。
     * 启动时检测 JPA/MyBatis 用户模块配置组合是否合法。
     */
    public static class SecurityUserDataSourceValidator {

        public SecurityUserDataSourceValidator(SecurityProperties properties) {
            validate(properties);
        }

        private void validate(SecurityProperties properties) {
            boolean jpaEnabled = properties.getUser().getJpa().isEnabled();
            boolean mybatisEnabled = properties.getUser().getMybatis().isEnabled();
            if (jpaEnabled && mybatisEnabled) {
                throw new IllegalStateException("athena.security.user.jpa.enabled and athena.security.user.mybatis.enabled cannot both be true");
            }
        }
    }
}
