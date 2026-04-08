package org.athena.framework.security.api.principal;

/**
 * 轻量身份主体。
 * 用于身份查询阶段，不携带密码等敏感凭据字段。
 */
public record IdentityPrincipal(
    String userId,
    String username,
    String tenantId,
    String userType,
    String displayName,
    String status
) {
}
