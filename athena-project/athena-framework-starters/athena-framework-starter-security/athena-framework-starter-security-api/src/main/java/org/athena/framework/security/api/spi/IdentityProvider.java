package org.athena.framework.security.api.spi;

import org.athena.framework.security.api.principal.IdentityPrincipal;

import java.util.Optional;

public interface IdentityProvider {

    Optional<IdentityPrincipal> loadByUsername(String username);

    Optional<IdentityPrincipal> loadByUserId(String userId);
}
