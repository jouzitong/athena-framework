package org.athena.framework.security.api.model;

import java.util.Map;

/**
 * 用户上下文只读视图。
 * 由认证过滤器写入线程上下文，业务侧在请求生命周期内读取使用。
 */
public interface UserContext {
    /**
     * 获取当前用户主体信息。
     */
    Subject subject();

    /**
     * 获取当前用户的认证状态。
     */
    AuthnState authn();

    /**
     * 获取当前用户的授权快照。
     */
    AuthorizationSnapshot authorization();

    /**
     * 获取当前用户的会话状态。
     */
    SessionState session();

    /**
     * 获取当前用户的扩展属性。
     */
    Map<String, Object> attributes();
}
