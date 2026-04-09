package org.athena.framework.security.user.jpa.service.rbac;

import org.athena.framework.security.api.spi.AuthorizationProvider;
import org.athena.framework.security.api.spi.RolePermissionResolver;
import org.athena.framework.security.api.spi.RoleProvider;

import java.util.Set;

public class JpaRbacAuthorizationProvider implements AuthorizationProvider {

    private final RoleProvider roleProvider;

    private final RolePermissionResolver rolePermissionResolver;

    public JpaRbacAuthorizationProvider(RoleProvider roleProvider,
                                        RolePermissionResolver rolePermissionResolver) {
        this.roleProvider = roleProvider;
        this.rolePermissionResolver = rolePermissionResolver;
    }

    @Override
    public Set<String> permissions(String userId, String tenantId) {
        Set<String> roles = roleProvider.roles(userId, tenantId);
        return rolePermissionResolver.permissions(roles, userId, tenantId);
    }
}
