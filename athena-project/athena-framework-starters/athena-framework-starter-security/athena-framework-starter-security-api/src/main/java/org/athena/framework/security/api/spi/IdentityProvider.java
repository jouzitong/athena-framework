package org.athena.framework.security.api.spi;

import org.athena.framework.security.api.principal.IdentityPrincipal;

import java.util.Optional;

/**
 * 身份信息提供者扩展点。
 * 面向用户名或用户 ID 提供用户主体查询能力。
 */
public interface IdentityProvider {

    Optional<IdentityPrincipal> loadByUsername(String username);

    Optional<IdentityPrincipal> loadByUserId(String userId);
}
