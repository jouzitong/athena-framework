package org.athena.framework.security.user.jpa.service.rbac;

import org.athena.framework.security.api.model.AuthorizationSnapshot;
import org.athena.framework.security.api.model.MutableUserContext;
import org.athena.framework.security.api.spi.RolePermissionResolver;
import org.athena.framework.security.api.spi.RoleProvider;
import org.athena.framework.security.api.spi.UserContextEnricher;

import java.util.LinkedHashSet;
import java.util.Set;

public class RbacUserContextEnricher implements UserContextEnricher {

    private final RoleProvider roleProvider;

    private final RolePermissionResolver rolePermissionResolver;

    public RbacUserContextEnricher(RoleProvider roleProvider,
                                   RolePermissionResolver rolePermissionResolver) {
        this.roleProvider = roleProvider;
        this.rolePermissionResolver = rolePermissionResolver;
    }

    @Override
    public int order() {
        return 20;
    }

    @Override
    public void enrich(MutableUserContext context) {
        if (context.subject() == null) {
            return;
        }
        Set<String> roles = roleProvider.roles(context.subject().userId(), context.subject().tenantId());
        Set<String> permissions = rolePermissionResolver.permissions(roles, context.subject().userId(), context.subject().tenantId());
        Set<String> scopes = context.authorization() == null || context.authorization().scopes() == null
            ? Set.of()
            : new LinkedHashSet<>(context.authorization().scopes());
        context.setAuthorization(new AuthorizationSnapshot(permissions, roles, scopes));
        context.attributes().put("roles", roles);
        context.attributes().put("permissions", permissions);
    }
}
