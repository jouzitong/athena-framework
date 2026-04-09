package org.athena.framework.security.user.jpa.repository;

import org.athena.framework.security.user.jpa.entity.SecRolePermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface SecRolePermissionJpaRepository extends JpaRepository<SecRolePermissionEntity, Long> {

    List<SecRolePermissionEntity> findByRoleCodeIn(Collection<String> roleCodes);
}
