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
    public SecurityTokenTypeValidator securityTokenTypeValidator(SecurityProperties properties, TokenManager tokenManager) {
        return new SecurityTokenTypeValidator(properties, tokenManager);
    }

    public static class SecurityTokenTypeValidator {

        private final SecurityProperties properties;

        private final TokenManager tokenManager;

        public SecurityTokenTypeValidator(SecurityProperties properties, TokenManager tokenManager) {
            this.properties = properties;
            this.tokenManager = tokenManager;
            validate();
        }

        private void validate() {
            String tokenType = properties.getToken().getType();
            if ("local".equalsIgnoreCase(tokenType)) {
                return;
            }

            if ("jwt".equalsIgnoreCase(tokenType)) {
                if (!properties.getToken().getJwt().isEnabled()) {
                    throw new IllegalStateException("athena.security.token.type=jwt but athena.security.token.jwt.enabled=false");
                }
                if (tokenManager instanceof LocalTokenManager) {
                    throw new IllegalStateException("athena.security.token.type=jwt but JWT token module is not active");
                }
                return;
            }

            if ("redis".equalsIgnoreCase(tokenType)) {
                if (!properties.getToken().getRedis().isEnabled()) {
                    throw new IllegalStateException("athena.security.token.type=redis but athena.security.token.redis.enabled=false");
                }
                if (tokenManager instanceof LocalTokenManager) {
                    throw new IllegalStateException("athena.security.token.type=redis but redis token module is not active");
                }
                return;
            }

            throw new IllegalStateException("Unsupported token type: " + tokenType);
        }
    }
}
