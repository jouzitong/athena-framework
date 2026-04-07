package org.athena.framework.security.user.jpa.repository;

import org.athena.framework.security.user.jpa.entity.SecUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SecUserJpaRepository extends JpaRepository<SecUserEntity, Long> {

    Optional<SecUserEntity> findByUsername(String username);

    Optional<SecUserEntity> findByUserId(String userId);
}
