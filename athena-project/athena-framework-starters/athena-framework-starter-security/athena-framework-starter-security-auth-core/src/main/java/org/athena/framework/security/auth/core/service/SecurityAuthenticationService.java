package org.athena.framework.security.auth.core.service;

import org.athena.framework.security.api.auth.AuthenticationRequest;
import org.athena.framework.security.api.auth.AuthenticationResult;
import org.athena.framework.security.api.event.AuthenticationFailureEvent;
import org.athena.framework.security.api.event.AuthenticationSuccessEvent;
import org.athena.framework.security.api.model.MutableUserContext;
import org.athena.framework.security.api.model.SessionState;
import org.athena.framework.security.api.model.UserContext;
import org.athena.framework.security.api.spi.Authenticator;
import org.athena.framework.security.api.spi.TokenManager;
import org.athena.framework.security.api.spi.UserContextEnricher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

/**
 * 认证应用服务。
 * 编排认证、上下文增强、令牌签发与认证事件发布。
 */
public class SecurityAuthenticationService implements SecurityAuthenticationFacade {
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityAuthenticationService.class);

    private final Authenticator authenticator;

    private final TokenManager tokenManager;

    private final List<UserContextEnricher> enrichers;

    private final ApplicationEventPublisher eventPublisher;

    public SecurityAuthenticationService(Authenticator authenticator,
                                         TokenManager tokenManager,
                                         List<UserContextEnricher> enrichers,
                                         ApplicationEventPublisher eventPublisher) {
        this.authenticator = authenticator;
        this.tokenManager = tokenManager;
        this.enrichers = enrichers.stream().sorted(Comparator.comparingInt(UserContextEnricher::order)).toList();
        this.eventPublisher = eventPublisher;
    }

    @Override
    public AuthenticationResult authenticate(AuthenticationRequest request) {
        AuthenticationResult result = authenticator.authenticate(request);
        if (!result.success() || result.context() == null) {
            LOGGER.warn("Authentication failed, username={}, code={}, message={}",
                request.username(), result.code(), result.message());
            eventPublisher.publishEvent(new AuthenticationFailureEvent(request.username(), result.code(), result.message()));
            return result;
        }

        MutableUserContext context = result.context();
        for (UserContextEnricher enricher : enrichers) {
            enricher.enrich(context);
        }

        String token = tokenManager.create(context);
        context.setSession(context.session() == null ? null :
            new org.athena.framework.security.api.model.SessionState(
                context.session().sessionId(),
                token,
                context.session().issuedAt(),
                context.session().expireAt()));
        LOGGER.info("Authentication success, userId={}, username={}",
            context.subject() == null ? null : context.subject().userId(),
            context.subject() == null ? null : context.subject().username());
        eventPublisher.publishEvent(new AuthenticationSuccessEvent(context));
        return AuthenticationResult.success(context);
    }

    @Override
    public void logout(String token) {
        if (token == null || token.isBlank()) {
            LOGGER.debug("Logout skipped because token is empty");
            return;
        }
        tokenManager.invalidate(token);
        LOGGER.info("Logout completed");
    }

    /**
     * 刷新 token 并返回新的认证结果。
     *
     * 核心作用：
     * 基于旧 token 解析用户上下文，重新执行上下文增强，签发新 token 并清理旧 token，延长登录态有效时间。
     *
     * 使用说明：
     * 控制层应在请求头中携带当前 token 调用此方法；若返回失败（如 token 无效）应引导用户重新登录。
     *
     * @param token 当前请求携带的旧 token
     * @return 刷新后的认证结果，成功时包含新 token
     */
    @Override
    public AuthenticationResult refresh(String token) {
        if (token == null || token.isBlank()) {
            LOGGER.warn("Refresh failed because token is empty");
            return AuthenticationResult.failed("TOKEN_EMPTY", "token is empty");
        }
        UserContext userContext = tokenManager.parse(token);
        if (userContext == null || userContext.subject() == null) {
            LOGGER.warn("Refresh failed because token is invalid");
            return AuthenticationResult.failed("TOKEN_INVALID", "token is invalid");
        }

        MutableUserContext mutableUserContext = toMutableUserContext(userContext);
        for (UserContextEnricher enricher : enrichers) {
            enricher.enrich(mutableUserContext);
        }

        String refreshedToken = tokenManager.create(mutableUserContext);
        SessionState oldSession = mutableUserContext.session();
        mutableUserContext.setSession(new SessionState(
            oldSession == null || oldSession.sessionId() == null ? UUID.randomUUID().toString() : oldSession.sessionId(),
            refreshedToken,
            Instant.now(),
            oldSession == null ? null : oldSession.expireAt()
        ));
        tokenManager.invalidate(token);
        LOGGER.info("Refresh success, userId={}, username={}",
            mutableUserContext.subject().userId(), mutableUserContext.subject().username());
        return AuthenticationResult.success(mutableUserContext);
    }

    private MutableUserContext toMutableUserContext(UserContext userContext) {
        if (userContext instanceof MutableUserContext mutableUserContext) {
            return mutableUserContext;
        }
        MutableUserContext mutableUserContext = new MutableUserContext();
        mutableUserContext.setSubject(userContext.subject());
        mutableUserContext.setAuthn(userContext.authn());
        mutableUserContext.setAuthorization(userContext.authorization());
        mutableUserContext.setSession(userContext.session());
        if (userContext.attributes() != null && !userContext.attributes().isEmpty()) {
            mutableUserContext.getAttributes().putAll(new LinkedHashMap<>(userContext.attributes()));
        }
        return mutableUserContext;
    }
}
