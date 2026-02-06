package org.athena.framework.websocket.security;

/**
 * Token 解析接口
 */
public interface TokenService {

    /**
     * 解析 Token，返回用户信息
     */
    TokenInfo parse(String token);
}
