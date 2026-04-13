package org.athena.framework.security.auth.core.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.arthena.framework.common.constant.CodeConstant;
import org.athena.framework.security.api.spi.SecurityAuthAttributes;
import org.athena.framework.security.api.spi.TokenParseStatus;
import org.athena.framework.security.api.model.UserContext;
import org.springframework.http.HttpMethod;

import java.io.IOException;

/**
 * 默认 token 强制拦截实现。
 * 非忽略路径下，token 缺失或解析失败时返回 401。
 *
 * 注意：该实现不会被框架自动注册为 Bean，
 * 业务方可按需声明为 Spring Bean 以启用。
 */
public class RequireTokenSecurityRequestInterceptor implements SecurityRequestInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             String token,
                             UserContext userContext,
                             boolean ignored) throws IOException {
        if (ignored) {
            return true;
        }
        // 解决 CORS 预检请求问题
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }
        if (StringUtils.isBlank(token) || userContext == null) {
            int code;
            if (StringUtils.isBlank(token)) {
                code = CodeConstant.UNAUTHORIZED;
            } else {
                TokenParseStatus tokenParseStatus = (TokenParseStatus) request.getAttribute(SecurityAuthAttributes.TOKEN_PARSE_STATUS);
                if (tokenParseStatus == TokenParseStatus.EXPIRED) {
                    code = CodeConstant.TOKEN_EXPIRED;
                } else {
                    code = CodeConstant.TOKEN_INVALID;
                }
            }
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, org.arthena.framework.common.utils.ErrorCodeUtils.getMsg(code));
            return false;
        }
        return true;
    }
}
