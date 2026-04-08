package org.athena.framework.security.api.spi;

import org.athena.framework.security.api.auth.AuthenticationRequest;
import org.athena.framework.security.api.auth.AuthenticationResult;

/**
 * 认证器扩展点。
 * 负责根据认证请求执行身份确认并产出认证结果。
 */
public interface Authenticator {

    AuthenticationResult authenticate(AuthenticationRequest request);
}
