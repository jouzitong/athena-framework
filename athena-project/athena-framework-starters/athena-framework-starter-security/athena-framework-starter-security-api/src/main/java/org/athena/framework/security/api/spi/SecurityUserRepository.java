package org.athena.framework.security.api.spi;

import org.athena.framework.security.api.principal.SecurityUser;

import java.util.Optional;

/**
 * 安全用户仓储扩展点。
 * 对外提供按用户名/用户 ID 获取安全用户聚合对象的能力。
 */
public interface SecurityUserRepository {

    Optional<SecurityUser> findByUsername(String username);

    Optional<SecurityUser> findByUserId(String userId);
}
