package org.athena.framework.security.api.spi;

import org.athena.framework.security.api.model.UserContext;

/**
 * 权限判定器扩展点。
 * 基于用户上下文判断是否具备目标权限。
 */
public interface PermissionEvaluator {

    boolean hasPermission(UserContext userContext, String permission);
}
