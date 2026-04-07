package org.athena.framework.security.api.auth;

public record AuthenticationRequest(
    String username,
    String password,
    String tenantId,
    String credentialType,
    String clientIp
) {
}
