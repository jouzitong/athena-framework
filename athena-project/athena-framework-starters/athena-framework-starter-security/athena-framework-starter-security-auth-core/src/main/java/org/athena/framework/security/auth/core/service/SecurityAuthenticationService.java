package org.athena.framework.security.auth.core.service;

import org.athena.framework.security.api.auth.AuthenticationRequest;
import org.athena.framework.security.api.auth.AuthenticationResult;
import org.athena.framework.security.api.event.AuthenticationFailureEvent;
import org.athena.framework.security.api.event.AuthenticationSuccessEvent;
import org.athena.framework.security.api.model.MutableUserContext;
import org.athena.framework.security.api.spi.Authenticator;
import org.athena.framework.security.api.spi.TokenManager;
import org.athena.framework.security.api.spi.UserContextEnricher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Comparator;
import java.util.List;

/**
 * 认证应用服务。
 * 编排认证、上下文增强、令牌签发与认证事件发布。
 */
public class SecurityAuthenticationService {
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

    public void logout(String token) {
        if (token == null || token.isBlank()) {
            LOGGER.debug("Logout skipped because token is empty");
            return;
        }
        tokenManager.invalidate(token);
        LOGGER.info("Logout completed");
    }
}
