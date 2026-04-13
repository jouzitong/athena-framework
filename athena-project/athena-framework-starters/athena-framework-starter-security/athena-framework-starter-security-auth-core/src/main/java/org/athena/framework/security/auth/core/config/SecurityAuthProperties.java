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

    /**
     * 是否强制要求非 ignoreUrls 的请求必须携带有效 token。
     * <p>
     * 开启后，将在认证过滤器之后启用默认拦截器，缺失/解析失败时返回 401。
     */
    private boolean requireToken = false;

    /**
     * 是否使用统一的 JSON 错误响应输出（与框架 HTTP 返回模型对齐）。
     * <p>
     * 当关闭时，仍可采用 servlet 容器默认的 sendError 行为。
     */
    private boolean jsonErrorResponse = true;
}
