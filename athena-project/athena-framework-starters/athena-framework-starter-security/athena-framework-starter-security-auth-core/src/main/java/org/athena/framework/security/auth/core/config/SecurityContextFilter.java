package org.athena.framework.security.auth.core.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.athena.framework.security.api.model.MutableUserContext;
import org.athena.framework.security.api.model.UserContext;
import org.athena.framework.security.api.spi.TokenManager;
import org.athena.framework.security.api.spi.UserContextEnricher;
import org.athena.framework.security.auth.core.context.SecurityContextHolder;
import org.athena.framework.security.auth.core.extractor.CredentialExtractor;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class SecurityContextFilter extends OncePerRequestFilter {

    private final CredentialExtractor credentialExtractor;

    private final TokenManager tokenManager;

    private final List<UserContextEnricher> enrichers;

    private final SecurityAuthProperties properties;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public SecurityContextFilter(CredentialExtractor credentialExtractor,
                                 TokenManager tokenManager,
                                 List<UserContextEnricher> enrichers,
                                 SecurityAuthProperties properties) {
        this.credentialExtractor = credentialExtractor;
        this.tokenManager = tokenManager;
        this.enrichers = enrichers.stream().sorted(Comparator.comparingInt(UserContextEnricher::order)).toList();
        this.properties = properties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            if (!isIgnored(request.getRequestURI())) {
                String token = credentialExtractor.extractToken(request);
                if (StringUtils.isNotBlank(token)) {
                    UserContext userContext = tokenManager.parse(token);
                    if (userContext != null) {
                        if (userContext instanceof MutableUserContext mutableUserContext) {
                            for (UserContextEnricher enricher : enrichers) {
                                enricher.enrich(mutableUserContext);
                            }
                        }
                        SecurityContextHolder.set(userContext);
                    }
                }
            }
            filterChain.doFilter(request, response);
        } finally {
            SecurityContextHolder.clear();
        }
    }

    private boolean isIgnored(String requestUri) {
        if (properties.getIgnoreUrls() == null || properties.getIgnoreUrls().isEmpty()) {
            return false;
        }
        return properties.getIgnoreUrls().stream().anyMatch(pattern -> antPathMatcher.match(pattern, requestUri));
    }
}
