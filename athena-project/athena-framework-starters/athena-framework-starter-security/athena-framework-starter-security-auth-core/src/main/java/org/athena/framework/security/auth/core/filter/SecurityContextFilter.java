package org.athena.framework.security.auth.core.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.athena.framework.security.api.model.MutableUserContext;
import org.athena.framework.security.api.model.UserContext;
import org.athena.framework.security.api.spi.TokenManager;
import org.athena.framework.security.api.spi.UserContextEnricher;
import org.athena.framework.security.auth.core.config.SecurityAuthProperties;
import org.athena.framework.security.auth.core.context.SecurityContextHolder;
import org.athena.framework.security.auth.core.extractor.CredentialExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

/**
 * 安全上下文过滤器。
 * 从请求中提取 token 并解析为 {@link UserContext}，随后绑定到线程上下文供后续链路使用。
 */
public class SecurityContextFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityContextFilter.class);

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
                        LOGGER.debug("Security context set for uri={}", request.getRequestURI());
                    } else {
                        LOGGER.debug("Token parsed to empty context, uri={}", request.getRequestURI());
                    }
                }
            } else {
                LOGGER.debug("Security filter ignored uri={}", request.getRequestURI());
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
