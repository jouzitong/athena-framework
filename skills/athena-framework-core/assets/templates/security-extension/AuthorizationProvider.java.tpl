package {{PACKAGE}}.security;

import org.athena.framework.security.api.spi.AuthorizationProvider;

import java.util.Set;

public final class {{NAME}}AuthorizationProvider implements AuthorizationProvider {

    private final PermissionLookup permissionLookup;

    public {{NAME}}AuthorizationProvider(PermissionLookup permissionLookup) {
        this.permissionLookup = permissionLookup;
    }

    @Override
    public Set<String> permissions(Long userId, String tenantId) {
        Set<String> permissions = permissionLookup.findPermissions(userId, tenantId);
        return permissions == null ? Set.of() : Set.copyOf(permissions);
    }

    @FunctionalInterface
    public interface PermissionLookup {
        Set<String> findPermissions(Long userId, String tenantId);
    }
}
