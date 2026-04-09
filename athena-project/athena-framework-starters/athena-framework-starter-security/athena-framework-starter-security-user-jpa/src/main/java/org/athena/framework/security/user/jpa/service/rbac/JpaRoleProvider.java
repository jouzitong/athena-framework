package org.athena.framework.security.user.jpa.service.rbac;

import org.apache.commons.lang3.StringUtils;
import org.athena.framework.security.api.spi.RoleProvider;
import org.athena.framework.security.user.jpa.entity.SecRoleEntity;
import org.athena.framework.security.user.jpa.entity.SecUserRoleEntity;
import org.athena.framework.security.user.jpa.repository.SecRoleJpaRepository;
import org.athena.framework.security.user.jpa.repository.SecUserRoleJpaRepository;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class JpaRoleProvider implements RoleProvider {

    private final SecUserRoleJpaRepository userRoleRepository;

    private final SecRoleJpaRepository roleRepository;

    public JpaRoleProvider(SecUserRoleJpaRepository userRoleRepository,
                           SecRoleJpaRepository roleRepository) {
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public Set<String> roles(String userId, String tenantId) {
        Set<String> roleCodes = userRoleRepository.findByUserId(userId)
            .stream()
            .map(SecUserRoleEntity::getRoleCode)
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.toCollection(LinkedHashSet::new));
        if (roleCodes.isEmpty()) {
            return Set.of();
        }
        return roleRepository.findByRoleCodeIn(roleCodes)
            .stream()
            .filter(role -> StringUtils.equalsIgnoreCase(role.getStatus(), "ENABLED"))
            .map(SecRoleEntity::getRoleCode)
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
