package org.athena.framework.security.api.model;

import java.util.Map;

/**
 * 用户上下文只读视图。
 * 由认证过滤器写入线程上下文，业务侧在请求生命周期内读取使用。
 */
public interface UserContext {

    Subject subject();

    AuthnState authn();

    AuthorizationSnapshot authorization();

    SessionState session();

    Map<String, Object> attributes();
}
