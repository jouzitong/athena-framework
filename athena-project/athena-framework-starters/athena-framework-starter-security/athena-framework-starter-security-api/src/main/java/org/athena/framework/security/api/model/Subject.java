package org.athena.framework.security.api.model;

public record Subject(
    String userId,
    String username,
    String tenantId,
    String userType
) {
}
