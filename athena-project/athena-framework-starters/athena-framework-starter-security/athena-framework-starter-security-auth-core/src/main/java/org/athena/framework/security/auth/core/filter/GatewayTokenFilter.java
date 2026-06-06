package org.athena.framework.security.auth.core.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.athena.framework.security.api.model.AuthorizationSnapshot;
import org.athena.framework.security.api.model.Subject;
import org.athena.framework.security.api.model.TokenContext;
import org.athena.framework.security.api.model.UserContext;
import org.athena.framework.security.api.spi.TokenManager;
import org.athena.framework.security.auth.core.config.SecurityAuthProperties;
import org.athena.framework.security.auth.core.http.HeaderAugmentingRequestWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.athena.framework.security.auth.core.contant.SecurityHeaderConstants.*;

//@Component
@Order(-120)
public class GatewayTokenFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(GatewayTokenFilter.class);


    private final SecurityAuthProperties properties;

    private final TokenManager tokenManager;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public GatewayTokenFilter(SecurityAuthProperties properties, TokenManager tokenManager) {
        this.properties = properties;
        this.tokenManager = tokenManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        LOGGER.debug("Gateway processing, uri={}", request.getRequestURI());
        if (!properties.isEnabled() || isOptions(request) || isIgnored(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorization = request.getHeader(properties.getTokenHeader());
        String token = extractToken(authorization, properties.getTokenPrefix());
        TokenContext tokenContext = tokenManager.parseV2(token);

        if (!tokenContext.authenticated()) {
            LOGGER.debug("Gateway token rejected, uri={}, status={}", request.getRequestURI(), tokenContext.status());
            if (properties.isRequireToken()) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setCharacterEncoding("UTF-8");
                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write("Unauthorized");
                return;
            }
        }

        filterChain.doFilter(enrichRequest(request, tokenContext.userContext()), response);
    }

    private boolean isOptions(HttpServletRequest request) {
        return "OPTIONS".equalsIgnoreCase(request.getMethod());
    }

    private boolean isIgnored(String requestUri) {
        if (!StringUtils.hasText(requestUri) || properties.getIgnoreUrls() == null || properties.getIgnoreUrls().isEmpty()) {
            return false;
        }
        return properties.getIgnoreUrls().stream().anyMatch(pattern -> antPathMatcher.match(pattern, requestUri));
    }

    private HttpServletRequest enrichRequest(HttpServletRequest request, UserContext userContext) {
        if (userContext == null) {
            return request;
        }

        String timestamp = String.valueOf(System.currentTimeMillis());
        String path = request.getRequestURI();
        Subject subject = userContext.subject();
        AuthorizationSnapshot authorizationSnapshot = userContext.authorization();

        String userId = resolveUserId(subject);
        String username = resolveUsername(subject);
        String tenantId = resolveTenantId(subject);
        String roles = resolveRoles(authorizationSnapshot);
        String sign = calculateSign(properties.getSigning().getSecret(), timestamp, path, userId, username, tenantId, roles);

        Map<String, List<String>> extraHeaders = new LinkedHashMap<>();
        extraHeaders.put(HEADER_USER_ID, List.of(userId));
        extraHeaders.put(HEADER_USERNAME, List.of(username));
        extraHeaders.put(HEADER_TENANT_ID, List.of(tenantId));
        extraHeaders.put(HEADER_ROLES, List.of(roles));
        extraHeaders.put(HEADER_TIMESTAMP, List.of(timestamp));
        extraHeaders.put(HEADER_PATH, List.of(path));
        extraHeaders.put(HEADER_SIGN, List.of(sign));
//        String authorization = request.getHeader(properties.getTokenHeader());
//        extraHeaders.put(properties.getTokenHeader(), List.of(authorization));

        return new HeaderAugmentingRequestWrapper(request, extraHeaders);
    }

    private String resolveUserId(Subject subject) {
        if (subject == null || subject.userId() == null) {
            return "";
        }
        return String.valueOf(subject.userId());
    }

    private String resolveUsername(Subject subject) {
        if (subject == null || !StringUtils.hasText(subject.username())) {
            return "";
        }
        return subject.username().trim();
    }

    private String resolveTenantId(Subject subject) {
        if (subject == null || !StringUtils.hasText(subject.tenantId())) {
            return "";
        }
        return subject.tenantId().trim();
    }

    private String resolveRoles(AuthorizationSnapshot authorizationSnapshot) {
        if (authorizationSnapshot == null || authorizationSnapshot.roles() == null || authorizationSnapshot.roles().isEmpty()) {
            return "";
        }
        return authorizationSnapshot.roles().stream()
                .filter(StringUtils::hasText)
                .map(String::trim)
                .sorted()
                .collect(Collectors.joining(","));
    }

    private String calculateSign(String secret,
                                 String timestamp,
                                 String path,
                                 String userId,
                                 String username,
                                 String tenantId,
                                 String roles) {
        String payload = String.join(SIGN_SEPARATOR,
                Objects.toString(timestamp, ""),
                Objects.toString(path, ""),
                Objects.toString(userId, ""),
                Objects.toString(username, ""),
                Objects.toString(tenantId, ""),
                Objects.toString(roles, ""));
        try {
            Mac mac = Mac.getInstance(SIGN_ALGORITHM);
            mac.init(new SecretKeySpec(Objects.toString(secret, "").getBytes(StandardCharsets.UTF_8), SIGN_ALGORITHM));
            return toHex(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to calculate gateway signature", exception);
        }
    }

    private String toHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder(bytes.length * 2);
        for (byte value : bytes) {
            builder.append(Character.forDigit((value >> 4) & 0x0F, 16));
            builder.append(Character.forDigit(value & 0x0F, 16));
        }
        return builder.toString();
    }

    private String extractToken(String authorization, String tokenPrefix) {
        if (!StringUtils.hasText(authorization)) {
            return null;
        }
        if (!StringUtils.hasText(tokenPrefix)) {
            return authorization.trim();
        }
        String prefix = tokenPrefix.trim();
        if (!StringUtils.hasText(prefix)) {
            return authorization.trim();
        }
        if (authorization.regionMatches(true, 0, prefix, 0, prefix.length())) {
            return authorization.substring(prefix.length()).trim();
        }
        return authorization.trim();
    }
}
