package org.athena.framework.security.user.jpa.repository;

import org.athena.framework.data.jpa.repository.BaseRepository;
import org.athena.framework.security.user.jpa.entity.SecUserCredentialEntity;

import java.util.Optional;

/**
 * 用户凭据 JPA 仓储。
 * 提供按 userId 与凭据类型查询凭据记录的能力。
 */
public interface SecUserCredentialJpaRepository extends BaseRepository<SecUserCredentialEntity> {

    Optional<SecUserCredentialEntity> findFirstByUserIdAndCredentialType(String userId, String credentialType);
}
