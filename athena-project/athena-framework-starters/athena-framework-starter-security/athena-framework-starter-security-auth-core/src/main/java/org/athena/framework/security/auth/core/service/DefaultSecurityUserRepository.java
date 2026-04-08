package org.athena.framework.security.auth.core.service;

import org.athena.framework.security.api.principal.SecurityUser;
import org.athena.framework.security.api.spi.SecurityUserRepository;

import java.util.Optional;

/**
 * 默认安全用户仓储占位实现。
 * 当未接入具体用户模块时返回空结果，避免自动配置缺失导致启动失败。
 */
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
