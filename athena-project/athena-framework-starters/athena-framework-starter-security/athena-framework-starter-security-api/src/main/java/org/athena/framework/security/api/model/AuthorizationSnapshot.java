package org.athena.framework.security.api.model;

import java.util.Set;

/**
 * 授权信息快照。
 * 包含权限、角色和数据范围等授权维度，便于一次性缓存到上下文中。
 */
public record AuthorizationSnapshot(
    Set<String> permissions,
    Set<String> roles,
    Set<String> scopes
) {
}
