package org.athena.framework.security.user.jpa.repository;

import org.athena.framework.data.jpa.repository.BaseRepository;
import org.athena.framework.security.user.jpa.entity.SecRoleEntity;

import java.util.Collection;
import java.util.List;

public interface SecRoleJpaRepository extends BaseRepository<SecRoleEntity> {

    List<SecRoleEntity> findByRoleCodeIn(Collection<String> roleCodes);
}
