package org.athena.framework.security.user.jpa.repository;

import org.athena.framework.security.user.jpa.entity.SecUserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SecUserRoleJpaRepository extends JpaRepository<SecUserRoleEntity, Long> {

    List<SecUserRoleEntity> findByUserId(String userId);
}
