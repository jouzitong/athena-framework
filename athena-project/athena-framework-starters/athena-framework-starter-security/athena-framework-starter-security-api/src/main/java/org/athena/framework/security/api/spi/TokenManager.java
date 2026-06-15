package org.athena.framework.security.api.spi;

import org.athena.framework.security.api.model.TokenContext;
import org.athena.framework.security.api.model.UserContext;

/**
 * 令牌管理器扩展点。
 * 负责令牌签发、解析与失效处理。
 */
public interface TokenManager {


    /**
     * 生成一个新的令牌。
     *
     * @param context 用户上下文
     * @return 生成的令牌字符串
     */
    String create(UserContext context);

    /**
     * 解析给定的令牌并返回用户上下文。
     *
     * @param token 令牌字符串
     * @return 用户上下文，如果解析失败则返回 null
     */
    @Deprecated
    UserContext parse(String token);

    TokenContext parseV2(String token);

    /**
     * 使给定的令牌失效。
     *
     * @param token 令牌字符串
     */
    void invalidate(String token);
}
