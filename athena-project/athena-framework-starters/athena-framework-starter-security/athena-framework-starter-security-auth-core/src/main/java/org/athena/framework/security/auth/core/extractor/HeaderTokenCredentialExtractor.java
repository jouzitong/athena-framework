package org.athena.framework.security.auth.core.extractor;

import jakarta.servlet.http.HttpServletRequest;
import org.arthena.framework.common.constant.RequestHeaderConstant;
import org.apache.commons.lang3.StringUtils;

/**
 * 基于请求头的 token 提取器。
 * 优先读取配置头，其次回退到标准 Authorization 头和 token 请求参数。
 */
public class HeaderTokenCredentialExtractor implements CredentialExtractor {

    private final String tokenHeader;

    private final String tokenPrefix;

    public HeaderTokenCredentialExtractor(String tokenHeader, String tokenPrefix) {
        this.tokenHeader = tokenHeader;
        this.tokenPrefix = tokenPrefix;
    }

    @Override
    public String extractToken(HttpServletRequest request) {
        String token = request.getHeader(tokenHeader);
        if (StringUtils.isBlank(token) && !RequestHeaderConstant.AUTHORIZATION.equalsIgnoreCase(tokenHeader)) {
            token = request.getHeader(RequestHeaderConstant.AUTHORIZATION);
        }
        if (StringUtils.isBlank(token)) {
            token = request.getParameter(RequestHeaderConstant.TOKEN);
        }
        if (StringUtils.isBlank(token)) {
            return null;
        }
        if (StringUtils.isBlank(tokenPrefix)) {
            return token;
        }
        String normalizedPrefix = tokenPrefix.trim() + " ";
        return token.startsWith(normalizedPrefix) ? token.substring(normalizedPrefix.length()).trim() : token;
    }
}
