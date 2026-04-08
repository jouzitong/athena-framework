package org.athena.framework.security.user.jpa.service;

import org.athena.framework.security.api.principal.SecurityUser;
import org.athena.framework.security.api.spi.SecurityUserRepository;
import org.athena.framework.security.user.jpa.entity.SecUserCredentialEntity;
import org.athena.framework.security.user.jpa.entity.SecUserEntity;
import org.athena.framework.security.user.jpa.repository.SecUserCredentialJpaRepository;
import org.athena.framework.security.user.jpa.repository.SecUserJpaRepository;

import java.util.Optional;

/**
 * JPA 安全用户仓储适配器。
 * 聚合用户主表与凭据表，组装为统一的 {@link SecurityUser} 领域对象。
 */
public class JpaSecurityUserRepository implements SecurityUserRepository {

    private static final String CREDENTIAL_TYPE_PASSWORD = "PASSWORD";

    private final SecUserJpaRepository userRepository;

    private final SecUserCredentialJpaRepository credentialRepository;

    public JpaSecurityUserRepository(SecUserJpaRepository userRepository,
                                     SecUserCredentialJpaRepository credentialRepository) {
        this.userRepository = userRepository;
        this.credentialRepository = credentialRepository;
    }

    @Override
    public Optional<SecurityUser> findByUsername(String username) {
        return userRepository.findByUsername(username).map(this::toSecurityUser);
    }

    @Override
    public Optional<SecurityUser> findByUserId(String userId) {
        return userRepository.findByUserId(userId).map(this::toSecurityUser);
    }

    private SecurityUser toSecurityUser(SecUserEntity userEntity) {
        SecUserCredentialEntity credential = credentialRepository
            .findFirstByUserIdAndCredentialType(userEntity.getUserId(), CREDENTIAL_TYPE_PASSWORD)
            .orElse(null);

        return new SecurityUser(
            userEntity.getUserId(),
            userEntity.getUsername(),
            userEntity.getDisplayName(),
            userEntity.getTenantId(),
            "USER",
            userEntity.getStatus(),
            credential == null ? null : credential.getPasswordHash(),
            credential == null ? null : credential.getPasswordAlgo(),
            credential == null ? null : credential.getPasswordSalt()
        );
    }
}
