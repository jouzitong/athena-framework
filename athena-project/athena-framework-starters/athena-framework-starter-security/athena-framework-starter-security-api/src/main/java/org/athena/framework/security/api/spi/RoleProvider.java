package org.athena.framework.security.api.spi;

import java.util.Set;

/**
 * 角色提供者扩展点。
 * 根据用户标识与租户查询当前用户角色集合。
 */
public interface RoleProvider {

    Set<String> roles(String userId, String tenantId);
}
