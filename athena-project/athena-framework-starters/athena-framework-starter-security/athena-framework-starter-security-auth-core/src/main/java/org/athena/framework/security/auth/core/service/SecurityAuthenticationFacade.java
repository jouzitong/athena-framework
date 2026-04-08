package org.athena.framework.security.auth.core.service;

import org.athena.framework.security.api.auth.AuthenticationRequest;
import org.athena.framework.security.api.auth.AuthenticationResult;

/**
 * 认证服务门面接口。
 * 对外统一暴露登录认证、令牌刷新与登出能力，便于控制器与业务方按接口依赖。
 */
public interface SecurityAuthenticationFacade {

    /**
     * 执行账号认证并在成功后签发 token。
     *
     * @param request 认证请求参数
     * @return 认证结果；成功时包含用户上下文与会话 token
     */
    AuthenticationResult authenticate(AuthenticationRequest request);

    /**
     * 使当前 token 失效（若 token 管理器支持状态化失效）。
     *
     * @param token 待失效 token
     */
    void logout(String token);

    /**
     * 刷新 token 并返回新的认证结果。
     *
     * 核心作用：
     * 基于现有有效 token 生成新的 token，更新会话签发时间，并复用上下文增强链路，避免用户重复登录。
     *
     * 使用说明：
     * 调用方应在用户登录态仍有效时传入旧 token；若 token 无效/过期会返回失败结果。
     * 刷新成功后应立即使用返回的新 token 替换旧 token；旧 token 会按具体实现进行失效处理。
     *
     * @param token 旧 token
     * @return 刷新结果；成功时 context.session.tokenId 为新 token
     */
    AuthenticationResult refresh(String token);
}
