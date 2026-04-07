package org.athena.framework.security.token.jwt.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.athena.framework.security.api.spi.TokenManager;
import org.athena.framework.security.starter.marker.SecurityCoreMarker;
import org.athena.framework.security.token.jwt.service.JwtTokenManager;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnBean(SecurityCoreMarker.class)
@ConditionalOnProperty(prefix = "athena.security.token.jwt", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(JwtTokenProperties.class)
public class SecurityJwtTokenAutoConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "athena.security.token", name = "type", havingValue = "jwt")
    @ConditionalOnMissingBean(TokenManager.class)
    public TokenManager tokenManager(ObjectMapper objectMapper, JwtTokenProperties properties) {
        return new JwtTokenManager(objectMapper, properties);
    }
}
