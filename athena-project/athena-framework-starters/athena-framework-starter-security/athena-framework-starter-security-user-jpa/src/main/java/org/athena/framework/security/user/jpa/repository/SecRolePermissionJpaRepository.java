package org.athena.framework.security.user.jpa.repository;

import org.athena.framework.data.jpa.repository.BaseRepository;
import org.athena.framework.security.user.jpa.entity.SecRolePermissionEntity;

import java.util.Collection;
import java.util.List;

public interface SecRolePermissionJpaRepository extends BaseRepository<SecRolePermissionEntity> {

    List<SecRolePermissionEntity> findByRoleCodeIn(Collection<String> roleCodes);
}
