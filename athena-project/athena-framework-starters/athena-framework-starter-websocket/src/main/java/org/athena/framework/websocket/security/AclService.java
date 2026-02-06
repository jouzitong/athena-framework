package org.athena.framework.websocket.security;

/**
 * ACL 权限校验接口
 */
public interface AclService {

    /**
     * 是否允许订阅
     */
    boolean canSubscribe(TokenInfo user, String topic);

    /**
     * 是否允许发布
     */
    boolean canPublish(TokenInfo user, String topic);

    /**
     * 是否允许请求
     */
    boolean canRequest(TokenInfo user, String action);
}
