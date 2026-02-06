package org.athena.framework.websocket.security;

public class AllowAllAclService implements AclService {

    @Override
    public boolean canSubscribe(TokenInfo user, String topic) {
        return true;
    }

    @Override
    public boolean canPublish(TokenInfo user, String topic) {
        return true;
    }

    @Override
    public boolean canRequest(TokenInfo user, String action) {
        return true;
    }
}
