package org.athena.framework.websocket.security;

public class AllowAllAclService implements AclService {

    @Override
    public boolean canSubscribe(TokenInfo user, String topic) {
        // 默认全部放行
        return true;
    }

    @Override
    public boolean canPublish(TokenInfo user, String topic) {
        // 默认全部放行
        return true;
    }

    @Override
    public boolean canRequest(TokenInfo user, String action) {
        // 默认全部放行
        return true;
    }
}
