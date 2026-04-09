package org.athena.framework.security.api.spi;

import java.util.Set;

/**
 * 角色权限解析器扩展点。
 * 根据角色集合解析其关联的权限编码。
 */
public interface RolePermissionResolver {

    Set<String> permissions(Set<String> roles, String userId, String tenantId);
}
