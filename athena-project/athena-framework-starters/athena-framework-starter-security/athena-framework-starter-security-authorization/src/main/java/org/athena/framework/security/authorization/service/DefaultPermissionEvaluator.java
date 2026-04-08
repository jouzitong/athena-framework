package org.athena.framework.security.authorization.service;

import org.athena.framework.security.api.model.AuthorizationSnapshot;
import org.athena.framework.security.api.model.UserContext;
import org.athena.framework.security.api.spi.AuthorizationProvider;
import org.athena.framework.security.api.spi.PermissionEvaluator;

import java.util.Collections;
import java.util.Set;

/**
 * 默认权限判定器。
 * 优先使用上下文缓存权限，缺失时回退到 {@link AuthorizationProvider} 实时加载。
 */
public class DefaultPermissionEvaluator implements PermissionEvaluator {

    private final AuthorizationProvider authorizationProvider;

    public DefaultPermissionEvaluator(AuthorizationProvider authorizationProvider) {
        this.authorizationProvider = authorizationProvider;
    }

    @Override
    public boolean hasPermission(UserContext userContext, String permission) {
        if (userContext == null || userContext.subject() == null) {
            return false;
        }

        AuthorizationSnapshot authorizationSnapshot = userContext.authorization();
        if (authorizationSnapshot != null && authorizationSnapshot.permissions() != null) {
            return authorizationSnapshot.permissions().contains(permission);
        }

        Set<String> permissions = authorizationProvider.permissions(userContext.subject().userId(), userContext.subject().tenantId());
        return permissions != null && permissions.contains(permission);
    }

    /**
     * 空授权提供者。
     * 当业务方未提供授权数据源时返回空权限集合。
     */
    public static class EmptyAuthorizationProvider implements AuthorizationProvider {

        @Override
        public Set<String> permissions(String userId, String tenantId) {
            return Collections.emptySet();
        }
    }
}
