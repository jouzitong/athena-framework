package org.athena.framework.security.auth.core.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.athena.framework.security.api.model.UserContext;

import java.io.IOException;

/**
 * 安全请求拦截器扩展点。
 * 在 {@link SecurityContextFilter} 解析 token 之后、进入业务处理之前执行。
 */
public interface SecurityRequestInterceptor {

    /**
     * 拦截顺序，值越小越先执行。
     */
    default int order() {
        return 0;
    }

    /**
     * 请求前置处理。
     *
     * @param request 当前请求
     * @param response 当前响应
     * @param token 请求中提取到的 token（可能为空）
     * @param userContext token 解析后的用户上下文（可能为空）
     * @param ignored 是否命中忽略路径
     * @return {@code true} 表示继续后续链路；{@code false} 表示拦截并终止
     */
    boolean preHandle(HttpServletRequest request,
                      HttpServletResponse response,
                      String token,
                      UserContext userContext,
                      boolean ignored) throws IOException;
}
