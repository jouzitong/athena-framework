package org.athena.framework.security.auth.core.gateway;

import jakarta.servlet.http.HttpServletRequest;
import org.athena.framework.security.api.model.AuthnState;
import org.athena.framework.security.api.model.AuthorizationSnapshot;
import org.athena.framework.security.api.model.MutableUserContext;
import org.athena.framework.security.api.model.SessionState;
import org.athena.framework.security.api.model.Subject;
import org.athena.framework.security.api.model.UserContext;
import org.athena.framework.security.auth.core.config.SecurityAuthProperties;
import org.springframework.util.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 从 gateway 透传请求头中恢复用户上下文并校验签名。
 */
public class GatewayRequestHeaderValidator {

    private static final String SIGN_ALGORITHM = "HmacSHA256";

    private static final String SIGN_SEPARATOR = "\n";

    private static final String AUTH_TYPE = GatewayRequestHeaderConstants.AUTH_TYPE;

    private final SecurityAuthProperties properties;

    private final Clock clock;

    private final long allowedClockSkewMillis;

    public GatewayRequestHeaderValidator(SecurityAuthProperties properties) {
        this(properties, Clock.systemUTC());
    }

    public GatewayRequestHeaderValidator(SecurityAuthProperties properties, Clock clock) {
        this(properties, clock, Duration.ofMinutes(5));
    }

    public GatewayRequestHeaderValidator(SecurityAuthProperties properties, Clock clock, Duration allowedClockSkew) {
        this.properties = Objects.requireNonNull(properties, "properties must not be null");
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
        Duration resolvedAllowedClockSkew = allowedClockSkew == null ? Duration.ofMinutes(5) : allowedClockSkew;
        this.allowedClockSkewMillis = Math.max(0L, resolvedAllowedClockSkew.toMillis());
    }

    public ValidationResult validate(HttpServletRequest request) {
        Headers headers = readHeaders(request);
        if (headers == null) {
            return ValidationResult.invalid("missing required gateway headers", null);
        }
        if (!StringUtils.hasText(headers.userId())) {
            return ValidationResult.invalid("missing X-User-Id", headers);
        }
        if (!StringUtils.hasText(headers.username())) {
            return ValidationResult.invalid("missing X-Username", headers);
        }
        if (!StringUtils.hasText(headers.tenantId())) {
            return ValidationResult.invalid("missing X-Tenant-Id", headers);
        }
        if (!StringUtils.hasText(headers.roles())) {
            return ValidationResult.invalid("missing X-Roles", headers);
        }
        if (!StringUtils.hasText(headers.timestamp())) {
            return ValidationResult.invalid("missing X-Timestamp", headers);
        }
        if (!StringUtils.hasText(headers.path())) {
            return ValidationResult.invalid("missing path", headers);
        }
        if (!StringUtils.hasText(headers.sign())) {
            return ValidationResult.invalid("missing X-Gateway-Sign", headers);
        }

        Long userId;
        try {
            userId = Long.parseLong(headers.userId());
        } catch (NumberFormatException exception) {
            return ValidationResult.invalid("invalid X-User-Id", headers);
        }

        long timestampMillis;
        try {
            timestampMillis = Long.parseLong(headers.timestamp());
        } catch (NumberFormatException exception) {
            return ValidationResult.invalid("invalid X-Timestamp", headers);
        }

        long nowMillis = clock.millis();
        if (Math.abs(nowMillis - timestampMillis) > allowedClockSkewMillis) {
            return ValidationResult.invalid("gateway header timestamp expired", headers);
        }

        String expectedPath = request == null ? null : request.getRequestURI();
        if (!Objects.equals(expectedPath, headers.path())) {
            return ValidationResult.invalid("gateway path mismatch", headers);
        }

        String expectedSign = calculateSign(
                properties.getSigning() == null ? null : properties.getSigning().getSecret(),
                headers.timestamp(),
                headers.path(),
                headers.userId(),
                headers.username(),
                headers.tenantId(),
                headers.roles()
        );
        if (!expectedSign.equalsIgnoreCase(headers.sign())) {
            return ValidationResult.invalid("gateway signature mismatch", headers);
        }

        UserContext userContext = buildUserContext(headers, userId, timestampMillis);
        return ValidationResult.valid(headers, userContext);
    }

    private Headers readHeaders(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        return new Headers(
                trimToNull(request.getHeader(GatewayRequestHeaderConstants.USER_ID)),
                trimToNull(request.getHeader(GatewayRequestHeaderConstants.USERNAME)),
                trimToNull(request.getHeader(GatewayRequestHeaderConstants.TENANT_ID)),
                trimToNull(request.getHeader(GatewayRequestHeaderConstants.ROLES)),
                trimToNull(request.getHeader(GatewayRequestHeaderConstants.TIMESTAMP)),
                trimToNull(request.getHeader(GatewayRequestHeaderConstants.PATH)),
                trimToNull(request.getHeader(GatewayRequestHeaderConstants.SIGN))
        );
    }

    private UserContext buildUserContext(Headers headers, Long userId, long timestampMillis) {
        MutableUserContext userContext = new MutableUserContext();
        userContext.setSubject(new Subject(userId, headers.username(), headers.tenantId(), null));
        userContext.setAuthn(new AuthnState(true, AUTH_TYPE, Instant.ofEpochMilli(timestampMillis)));
        userContext.setAuthorization(new AuthorizationSnapshot(null, parseRoles(headers.roles()), null));
        userContext.setSession(new SessionState(null, null, Instant.ofEpochMilli(timestampMillis), null));
        return userContext;
    }

    private Set<String> parseRoles(String roles) {
        if (!StringUtils.hasText(roles)) {
            return Set.of();
        }
        return Arrays.stream(roles.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .collect(Collectors.toCollection(LinkedHashSet::new));
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

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    public record Headers(
            String userId,
            String username,
            String tenantId,
            String roles,
            String timestamp,
            String path,
            String sign
    ) {
        public String signatureHeaderName() {
            return GatewayRequestHeaderConstants.SIGN;
        }
    }

    public record ValidationResult(boolean valid, String reason, Headers headers, UserContext userContext) {
        public static ValidationResult valid(Headers headers, UserContext userContext) {
            return new ValidationResult(true, null, headers, userContext);
        }

        public static ValidationResult invalid(String reason, Headers headers) {
            return new ValidationResult(false, reason, headers, null);
        }
    }
}
