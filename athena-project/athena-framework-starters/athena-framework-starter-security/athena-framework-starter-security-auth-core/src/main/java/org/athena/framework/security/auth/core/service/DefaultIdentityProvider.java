package org.athena.framework.security.auth.core.service;

import org.athena.framework.security.api.principal.IdentityPrincipal;
import org.athena.framework.security.api.principal.SecurityUser;
import org.athena.framework.security.api.spi.IdentityProvider;
import org.athena.framework.security.api.spi.SecurityUserRepository;

import java.util.Optional;

/**
 * 默认身份提供者。
 * 复用 {@link SecurityUserRepository} 查询用户并转换成轻量身份主体。
 */
public class DefaultIdentityProvider implements IdentityProvider {

    private final SecurityUserRepository securityUserRepository;

    public DefaultIdentityProvider(SecurityUserRepository securityUserRepository) {
        this.securityUserRepository = securityUserRepository;
    }

    @Override
    public Optional<IdentityPrincipal> loadByUsername(String username) {
        return securityUserRepository.findByUsername(username).map(this::toPrincipal);
    }

    @Override
    public Optional<IdentityPrincipal> loadByUserId(String userId) {
        return securityUserRepository.findByUserId(userId).map(this::toPrincipal);
    }

    private IdentityPrincipal toPrincipal(SecurityUser user) {
        return new IdentityPrincipal(user.userId(), user.username(), user.tenantId(), user.userType(), user.displayName(), user.status());
    }
}
