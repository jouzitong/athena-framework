package org.athena.framework.security.auth.core.service;

import lombok.extern.slf4j.Slf4j;
import org.athena.framework.security.api.auth.AuthenticationRequest;
import org.athena.framework.security.api.auth.AuthenticationResult;
import org.athena.framework.security.api.event.AuthenticationFailureEvent;
import org.athena.framework.security.api.event.AuthenticationSuccessEvent;
import org.athena.framework.security.api.model.MutableUserContext;
import org.athena.framework.security.api.spi.Authenticator;
import org.athena.framework.security.api.spi.TokenManager;
import org.athena.framework.security.api.spi.UserContextEnricher;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Comparator;
import java.util.List;

@Slf4j
public class SecurityAuthenticationService {

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
        eventPublisher.publishEvent(new AuthenticationSuccessEvent(context));
        return AuthenticationResult.success(context);
    }

    public void logout(String token) {
        if (token == null || token.isBlank()) {
            return;
        }
        tokenManager.invalidate(token);
    }
}
