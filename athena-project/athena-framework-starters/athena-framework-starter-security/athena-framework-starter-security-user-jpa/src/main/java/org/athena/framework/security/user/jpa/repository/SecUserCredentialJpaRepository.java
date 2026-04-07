package org.athena.framework.security.user.jpa.repository;

import org.athena.framework.security.user.jpa.entity.SecUserCredentialEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SecUserCredentialJpaRepository extends JpaRepository<SecUserCredentialEntity, Long> {

    Optional<SecUserCredentialEntity> findFirstByUserIdAndCredentialType(String userId, String credentialType);
}
