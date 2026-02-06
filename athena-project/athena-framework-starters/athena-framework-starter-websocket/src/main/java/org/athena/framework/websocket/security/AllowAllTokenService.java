package org.athena.framework.websocket.security;

import java.util.Collections;

public class AllowAllTokenService implements TokenService {

    @Override
    public TokenInfo parse(String token) {
        // 默认匿名用户
        return new TokenInfo("anonymous", Collections.emptyMap());
    }
}
