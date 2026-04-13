package org.athena.framework.security.token.jwt.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.athena.framework.security.api.model.MutableUserContext;
import org.athena.framework.security.api.model.UserContext;
import org.athena.framework.security.api.spi.TokenManager;
import org.athena.framework.security.api.spi.TokenManagerWithParseResult;
import org.athena.framework.security.api.spi.TokenParseResult;
import org.athena.framework.security.api.spi.TokenParseStatus;
import org.athena.framework.security.token.jwt.config.JwtTokenProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * JWT 令牌管理器。
 * 负责生成/解析基于 HS256 签名的 JWT，并将用户上下文写入 ctx 声明。
 */
public class JwtTokenManager implements TokenManagerWithParseResult {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenManager.class);

    private final ObjectMapper objectMapper;

    private final JwtTokenProperties properties;

    public JwtTokenManager(ObjectMapper objectMapper, JwtTokenProperties properties) {
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    @Override
    public String create(UserContext context) {
        try {
            long now = Instant.now().getEpochSecond();
            long exp = Instant.now().plusSeconds(properties.getAccessTokenExpireMinutes() * 60).getEpochSecond();

            Map<String, Object> header = Map.of("typ", "JWT", "alg", "HS256");
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("iat", now);
            payload.put("exp", exp);
            payload.put("ctx", toContextPayload(context));

            String encodedHeader = encode(objectMapper.writeValueAsBytes(header));
            String encodedPayload = encode(objectMapper.writeValueAsBytes(payload));
            String signature = sign(encodedHeader + "." + encodedPayload);
            LOGGER.debug("JWT token created, userId={}",
                context == null || context.subject() == null ? null : context.subject().userId());
            return encodedHeader + "." + encodedPayload + "." + signature;
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to create jwt token", ex);
        }
    }

    @Override
    public UserContext parse(String token) {
        return parseWithResult(token).getUserContext();
    }

    @Override
    public TokenParseResult parseWithResult(String token) {
        if (StringUtils.isBlank(token)) {
            LOGGER.debug("Skip parsing jwt token because token is blank");
            return new TokenParseResult(null, TokenParseStatus.EMPTY);
        }
        try {
            String[] chunks = token.split("\\.");
            if (chunks.length != 3) {
                LOGGER.debug("Invalid jwt token format");
                return new TokenParseResult(null, TokenParseStatus.INVALID_FORMAT);
            }
            String signingPayload = chunks[0] + "." + chunks[1];
            String expectedSignature = sign(signingPayload);
            if (!StringUtils.equals(expectedSignature, chunks[2])) {
                LOGGER.debug("JWT signature validation failed");
                return new TokenParseResult(null, TokenParseStatus.INVALID_SIGNATURE);
            }

            byte[] payloadBytes = Base64.getUrlDecoder().decode(chunks[1]);
            Map<String, Object> payload = objectMapper.readValue(payloadBytes, new TypeReference<>() {
            });
            Number exp = (Number) payload.get("exp");
            if (exp != null && Instant.now().getEpochSecond() > exp.longValue()) {
                LOGGER.debug("JWT token expired, exp={}", exp.longValue());
                return new TokenParseResult(null, TokenParseStatus.EXPIRED);
            }

            Object contextRaw = payload.get("ctx");
            if (contextRaw == null) {
                LOGGER.debug("JWT token missing ctx payload");
                return new TokenParseResult(null, TokenParseStatus.MISSING_CONTEXT);
            }
            return new TokenParseResult(objectMapper.convertValue(contextRaw, MutableUserContext.class), TokenParseStatus.OK);
        } catch (IllegalArgumentException ex) {
            // Base64 URL 解码可能抛 IllegalArgumentException
            LOGGER.debug("Invalid jwt token payload encoding: {}", ex.getMessage());
            return new TokenParseResult(null, TokenParseStatus.INVALID_FORMAT);
        } catch (Exception ex) {
            LOGGER.warn("Failed to parse jwt token: {}", ex.getMessage());
            return new TokenParseResult(null, TokenParseStatus.ERROR);
        }
    }

    @Override
    public void invalidate(String token) {
        // JWT 无状态，默认不做服务端失效。
    }

    private String sign(String content) throws Exception {
        Mac hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(properties.getSecret().getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmac.init(secretKeySpec);
        byte[] signature = hmac.doFinal(content.getBytes(StandardCharsets.UTF_8));
        return encode(signature);
    }

    private String encode(byte[] value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value);
    }

    private Map<String, Object> toContextPayload(UserContext context) {
        if (context == null) {
            return null;
        }
        Map<String, Object> contextPayload = new LinkedHashMap<>();
        contextPayload.put("subject", context.subject());
        contextPayload.put("authn", context.authn());
        contextPayload.put("authorization", context.authorization());
        contextPayload.put("session", context.session());
        contextPayload.put("attributes",
            context.attributes() == null ? Map.of() : new LinkedHashMap<>(context.attributes()));
        return contextPayload;
    }
}
