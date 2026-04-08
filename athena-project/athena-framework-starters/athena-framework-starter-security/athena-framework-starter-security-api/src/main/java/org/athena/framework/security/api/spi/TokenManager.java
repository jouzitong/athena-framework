package org.athena.framework.security.api.spi;

import org.athena.framework.security.api.model.UserContext;

/**
 * 令牌管理器扩展点。
 * 负责令牌签发、解析与失效处理。
 */
public interface TokenManager {

    String create(UserContext context);

    UserContext parse(String token);

    void invalidate(String token);
}
