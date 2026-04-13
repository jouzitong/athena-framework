package org.athena.framework.security.api.spi;

/**
 * 可选扩展：提供 token 解析的失败原因。
 * <p>
 * 兼容 {@link TokenManager#parse(String)} 的既有契约（返回 UserContext 或 null），
 * 在需要更精细提示语时，可通过 {@link #parseWithResult(String)} 获得解析状态。
 */
public interface TokenManagerWithParseResult extends TokenManager {

    TokenParseResult parseWithResult(String token);
}

