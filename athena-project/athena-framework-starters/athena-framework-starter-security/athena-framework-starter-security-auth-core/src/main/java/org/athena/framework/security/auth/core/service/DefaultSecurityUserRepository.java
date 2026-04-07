package org.athena.framework.security.auth.core.service;

import org.athena.framework.security.api.principal.SecurityUser;
import org.athena.framework.security.api.spi.SecurityUserRepository;

import java.util.Optional;

public class DefaultSecurityUserRepository implements SecurityUserRepository {

    @Override
    public Optional<SecurityUser> findByUsername(String username) {
        return Optional.empty();
    }

    @Override
    public Optional<SecurityUser> findByUserId(String userId) {
        return Optional.empty();
    }
}
