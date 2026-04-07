package org.athena.framework.security.token.jwt.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "athena.security.token.jwt")
public class JwtTokenProperties {

    private boolean enabled = false;

    private long accessTokenExpireMinutes = 120;

    private String secret = "athena-security-jwt-secret-change-me";
}
