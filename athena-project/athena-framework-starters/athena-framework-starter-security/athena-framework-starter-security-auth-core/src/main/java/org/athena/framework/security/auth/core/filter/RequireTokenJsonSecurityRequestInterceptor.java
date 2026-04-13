package org.athena.framework.security.auth.core.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.arthena.framework.common.constant.CodeConstant;
import org.athena.framework.security.api.model.UserContext;
import org.athena.framework.security.auth.core.web.SecurityHttpResponseWriter;

import java.io.IOException;

/**
 * token 强制拦截（JSON 输出版本）。
 * <p>
 * 非忽略路径下，token 缺失或解析失败时返回 401，并输出统一 JSON 响应。
 */
public class RequireTokenJsonSecurityRequestInterceptor implements SecurityRequestInterceptor {

    private final boolean jsonErrorResponse;

    public RequireTokenJsonSecurityRequestInterceptor(boolean jsonErrorResponse) {
        this.jsonErrorResponse = jsonErrorResponse;
    }

    @Override
    public int order() {
        return -100;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             String token,
                             UserContext userContext,
                             boolean ignored) throws IOException {
        if (ignored) {
            return true;
        }
        if (StringUtils.isBlank(token) || userContext == null) {
            if (jsonErrorResponse) {
                SecurityHttpResponseWriter.writeJson(response, HttpServletResponse.SC_UNAUTHORIZED, CodeConstant.UNAUTHORIZED);
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "unauthorized");
            }
            return false;
        }
        return true;
    }
}
