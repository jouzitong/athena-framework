package org.athena.framework.security.user.jpa.repository;

import org.athena.framework.security.user.jpa.entity.SecPermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface SecPermissionJpaRepository extends JpaRepository<SecPermissionEntity, Long> {

    List<SecPermissionEntity> findByPermissionCodeIn(Collection<String> permissionCodes);
}
