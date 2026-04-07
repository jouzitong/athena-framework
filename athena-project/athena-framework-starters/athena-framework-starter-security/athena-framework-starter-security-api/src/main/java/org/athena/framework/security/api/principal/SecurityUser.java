package org.athena.framework.security.api.principal;

public record SecurityUser(
    String userId,
    String username,
    String displayName,
    String tenantId,
    String userType,
    String status,
    String passwordHash,
    String passwordAlgo,
    String passwordSalt
) {
}
