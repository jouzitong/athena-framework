package org.athena.framework.security.authorization.service;

import org.athena.framework.security.api.model.AuthorizationSnapshot;
import org.athena.framework.security.api.model.UserContext;
import org.athena.framework.security.api.spi.AuthorizationProvider;
import org.athena.framework.security.api.spi.PermissionEvaluator;

import java.util.Collections;
import java.util.Set;

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

    public static class EmptyAuthorizationProvider implements AuthorizationProvider {

        @Override
        public Set<String> permissions(String userId, String tenantId) {
            return Collections.emptySet();
        }
    }
}
