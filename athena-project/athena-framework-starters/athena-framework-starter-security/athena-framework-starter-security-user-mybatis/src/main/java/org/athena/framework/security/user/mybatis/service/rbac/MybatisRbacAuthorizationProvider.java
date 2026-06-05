package org.athena.framework.security.user.mybatis.service.rbac;

import org.athena.framework.security.api.spi.AuthorizationProvider;
import org.athena.framework.security.api.spi.RolePermissionResolver;
import org.athena.framework.security.api.spi.RoleProvider;

import java.util.Set;

public class MybatisRbacAuthorizationProvider implements AuthorizationProvider {

    private final RoleProvider roleProvider;

    private final RolePermissionResolver rolePermissionResolver;

    public MybatisRbacAuthorizationProvider(RoleProvider roleProvider,
                                        RolePermissionResolver rolePermissionResolver) {
        this.roleProvider = roleProvider;
        this.rolePermissionResolver = rolePermissionResolver;
    }

    @Override
    public Set<String> permissions(Long userId, String tenantId) {
        Set<String> roles = roleProvider.roles(userId, tenantId);
        return rolePermissionResolver.permissions(roles, userId, tenantId);
    }
}
