package org.athena.framework.web.util;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.arthena.framework.common.utils.JacksonJsonUtils;
import org.athena.framework.web.filter.TraceIdFilter;
import org.athena.framework.web.properties.LibWebProperties;
import org.athena.framework.web.vo.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 响应签名工具
 */
public final class ResponseSignUtils {

    private static final Logger log = LoggerFactory.getLogger(ResponseSignUtils.class);
    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final ObjectMapper SIGN_MAPPER = JacksonJsonUtils.JSON.copy();

    static {
        SIGN_MAPPER.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        SIGN_MAPPER.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
    }

    private ResponseSignUtils() {
    }

    public static void sign(R<?> response, LibWebProperties.Sign signProperties) {
        if (response == null || signProperties == null || !signProperties.isEnabled()) {
            return;
        }
        if (!StringUtils.hasText(signProperties.getSecret())) {
            log.warn("lib.web.sign.enabled=true but lib.web.sign.secret is empty, skip response signing");
            return;
        }

        fillMetaIfAbsent(response);
        response.setSignKeyId(signProperties.getKeyId());
        response.setSign(hmacSha256(buildPayload(response), signProperties.getSecret()));
    }

    private static void fillMetaIfAbsent(R<?> response) {
        if (response.getTimestamp() == null) {
            response.setTimestamp(System.currentTimeMillis());
        }
        if (!StringUtils.hasText(response.getTraceId())) {
            response.setTraceId(MDC.get(TraceIdFilter.MDC_KEY));
        }
    }

    private static String buildPayload(R<?> response) {
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("code", response.getCode());
            payload.put("msg", response.getMsg());
            payload.put("timestamp", response.getTimestamp());
            payload.put("traceId", response.getTraceId());
            payload.put("data", response.getData());
            return SIGN_MAPPER.writeValueAsString(payload);
        } catch (Exception e) {
            throw new IllegalStateException("build response sign payload failed", e);
        }
    }

    private static String hmacSha256(String payload, String secret) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
            mac.init(secretKeySpec);
            byte[] digest = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(digest);
        } catch (Exception e) {
            throw new IllegalStateException("sign response failed", e);
        }
    }
}
