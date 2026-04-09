package org.athena.framework.security.user.jpa.service.rbac;

import org.apache.commons.lang3.StringUtils;
import org.athena.framework.security.api.spi.RolePermissionResolver;
import org.athena.framework.security.user.jpa.entity.SecPermissionEntity;
import org.athena.framework.security.user.jpa.entity.SecRolePermissionEntity;
import org.athena.framework.security.user.jpa.repository.SecPermissionJpaRepository;
import org.athena.framework.security.user.jpa.repository.SecRolePermissionJpaRepository;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class JpaRolePermissionResolver implements RolePermissionResolver {

    private final SecRolePermissionJpaRepository rolePermissionRepository;

    private final SecPermissionJpaRepository permissionRepository;

    public JpaRolePermissionResolver(SecRolePermissionJpaRepository rolePermissionRepository,
                                     SecPermissionJpaRepository permissionRepository) {
        this.rolePermissionRepository = rolePermissionRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public Set<String> permissions(Set<String> roles, String userId, String tenantId) {
        if (roles == null || roles.isEmpty()) {
            return Set.of();
        }
        Set<String> permissionCodes = rolePermissionRepository.findByRoleCodeIn(roles)
            .stream()
            .map(SecRolePermissionEntity::getPermissionCode)
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.toCollection(LinkedHashSet::new));
        if (permissionCodes.isEmpty()) {
            return Set.of();
        }
        return permissionRepository.findByPermissionCodeIn(permissionCodes)
            .stream()
            .filter(permission -> StringUtils.equalsIgnoreCase(permission.getStatus(), "ENABLED"))
            .map(SecPermissionEntity::getPermissionCode)
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
