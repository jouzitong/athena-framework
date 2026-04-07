package org.athena.framework.security.token.jwt.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.athena.framework.security.api.model.MutableUserContext;
import org.athena.framework.security.api.model.UserContext;
import org.athena.framework.security.api.spi.TokenManager;
import org.athena.framework.security.token.jwt.config.JwtTokenProperties;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

public class JwtTokenManager implements TokenManager {

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
            payload.put("ctx", context);

            String encodedHeader = encode(objectMapper.writeValueAsBytes(header));
            String encodedPayload = encode(objectMapper.writeValueAsBytes(payload));
            String signature = sign(encodedHeader + "." + encodedPayload);
            return encodedHeader + "." + encodedPayload + "." + signature;
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to create jwt token", ex);
        }
    }

    @Override
    public UserContext parse(String token) {
        if (StringUtils.isBlank(token)) {
            return null;
        }
        try {
            String[] chunks = token.split("\\.");
            if (chunks.length != 3) {
                return null;
            }
            String signingPayload = chunks[0] + "." + chunks[1];
            String expectedSignature = sign(signingPayload);
            if (!StringUtils.equals(expectedSignature, chunks[2])) {
                return null;
            }

            byte[] payloadBytes = Base64.getUrlDecoder().decode(chunks[1]);
            Map<String, Object> payload = objectMapper.readValue(payloadBytes, new TypeReference<>() {
            });
            Number exp = (Number) payload.get("exp");
            if (exp != null && Instant.now().getEpochSecond() > exp.longValue()) {
                return null;
            }

            Object contextRaw = payload.get("ctx");
            if (contextRaw == null) {
                return null;
            }
            return objectMapper.convertValue(contextRaw, MutableUserContext.class);
        } catch (Exception ex) {
            return null;
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
}
