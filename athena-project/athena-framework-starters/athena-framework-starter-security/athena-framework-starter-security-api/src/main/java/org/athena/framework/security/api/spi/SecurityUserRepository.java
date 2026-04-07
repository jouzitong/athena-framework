package org.athena.framework.security.api.spi;

import org.athena.framework.security.api.principal.SecurityUser;

import java.util.Optional;

public interface SecurityUserRepository {

    Optional<SecurityUser> findByUsername(String username);

    Optional<SecurityUser> findByUserId(String userId);
}
