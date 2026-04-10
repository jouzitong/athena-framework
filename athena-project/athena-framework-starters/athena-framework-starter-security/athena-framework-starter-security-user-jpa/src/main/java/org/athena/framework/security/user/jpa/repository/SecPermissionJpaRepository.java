package org.athena.framework.security.user.jpa.repository;

import org.athena.framework.data.jpa.repository.BaseRepository;
import org.athena.framework.security.user.jpa.entity.SecPermissionEntity;

import java.util.Collection;
import java.util.List;

public interface SecPermissionJpaRepository extends BaseRepository<SecPermissionEntity> {

    List<SecPermissionEntity> findByPermissionCodeIn(Collection<String> permissionCodes);
}
