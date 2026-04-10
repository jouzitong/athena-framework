package org.athena.framework.security.user.jpa.repository;

import org.athena.framework.data.jpa.repository.BaseRepository;
import org.athena.framework.security.user.jpa.entity.SecUserRoleEntity;

import java.util.List;

public interface SecUserRoleJpaRepository extends BaseRepository<SecUserRoleEntity> {

    List<SecUserRoleEntity> findByUserId(String userId);
}
