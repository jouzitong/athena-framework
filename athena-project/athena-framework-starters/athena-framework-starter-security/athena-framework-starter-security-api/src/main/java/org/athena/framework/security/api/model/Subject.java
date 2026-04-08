package org.athena.framework.security.api.model;

/**
 * 用户主体信息。
 * 标识当前请求代表的身份实体及其租户、用户类型。
 */
public record Subject(
    String userId,
    String username,
    String tenantId,
    String userType
) {
}
