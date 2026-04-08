package org.athena.framework.security.auth.core.extractor;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 凭据提取器扩展点。
 * 负责从 HTTP 请求中提取认证 token。
 */
public interface CredentialExtractor {

    String extractToken(HttpServletRequest request);
}
