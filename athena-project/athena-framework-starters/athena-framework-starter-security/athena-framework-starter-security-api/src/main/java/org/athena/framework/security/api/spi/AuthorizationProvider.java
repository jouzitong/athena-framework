package org.athena.framework.security.api.spi;

import java.util.Set;

/**
 * 授权数据提供者扩展点。
 * 根据用户标识与租户加载权限集合。
 */
public interface AuthorizationProvider {

    Set<String> permissions(String userId, String tenantId);
}
