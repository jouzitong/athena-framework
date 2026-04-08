package org.athena.framework.security.auth.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Data
/**
 * 认证模块配置项。
 * 定义 token 头、前缀和免认证 URL 列表等运行参数。
 */
@ConfigurationProperties(prefix = "athena.security.auth")
public class SecurityAuthProperties {

    private boolean enabled = true;

    private String tokenHeader = "Authorization";

    private String tokenPrefix = "Bearer";

    private List<String> ignoreUrls = new ArrayList<>();
}
