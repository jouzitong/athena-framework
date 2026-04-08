package org.athena.framework.security.token.jwt.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
/**
 * JWT 令牌配置项。
 * 控制 JWT 功能开关、过期时间和签名密钥。
 */
@ConfigurationProperties(prefix = "athena.security.token.jwt")
public class JwtTokenProperties {

    private boolean enabled = false;

    private long accessTokenExpireMinutes = 120;

    private String secret = "athena-security-jwt-secret-change-me";
}
