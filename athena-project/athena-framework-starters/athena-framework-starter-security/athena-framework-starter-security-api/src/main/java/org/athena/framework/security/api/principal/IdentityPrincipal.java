package org.athena.framework.security.api.principal;

public record IdentityPrincipal(
    String userId,
    String username,
    String tenantId,
    String userType,
    String displayName,
    String status
) {
}
