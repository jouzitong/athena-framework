package org.athena.framework.security.user.jpa.repository;

import org.athena.framework.security.user.jpa.entity.SecRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface SecRoleJpaRepository extends JpaRepository<SecRoleEntity, Long> {

    List<SecRoleEntity> findByRoleCodeIn(Collection<String> roleCodes);
}
