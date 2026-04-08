package org.athena.framework.security.api.principal;

/**
 * 安全域用户聚合对象。
 * 汇总用户主数据和认证凭据，是认证链路中的核心用户模型。
 */
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
