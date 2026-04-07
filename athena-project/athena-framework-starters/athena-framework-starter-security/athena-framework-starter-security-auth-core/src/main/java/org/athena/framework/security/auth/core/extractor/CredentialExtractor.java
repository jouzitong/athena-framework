package org.athena.framework.security.auth.core.extractor;

import jakarta.servlet.http.HttpServletRequest;

public interface CredentialExtractor {

    String extractToken(HttpServletRequest request);
}
