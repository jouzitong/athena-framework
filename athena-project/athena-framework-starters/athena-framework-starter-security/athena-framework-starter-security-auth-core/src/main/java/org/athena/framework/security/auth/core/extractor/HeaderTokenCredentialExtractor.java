package org.athena.framework.security.auth.core.extractor;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;

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
        if (StringUtils.isBlank(token) && !HttpHeaders.AUTHORIZATION.equalsIgnoreCase(tokenHeader)) {
            token = request.getHeader(HttpHeaders.AUTHORIZATION);
        }
        if (StringUtils.isBlank(token)) {
            token = request.getParameter("token");
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
