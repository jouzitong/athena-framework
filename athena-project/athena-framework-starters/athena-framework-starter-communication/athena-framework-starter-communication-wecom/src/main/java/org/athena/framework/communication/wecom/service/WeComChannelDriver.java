package org.athena.framework.communication.wecom.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.athena.framework.communication.api.ChannelDriver;
import org.athena.framework.communication.api.ChannelType;
import org.athena.framework.communication.api.Receiver;
import org.athena.framework.communication.api.ReceiverType;
import org.athena.framework.communication.api.SendRequest;
import org.athena.framework.communication.api.SendResult;
import org.athena.framework.communication.constant.CommunicationErrorCode;
import org.athena.framework.communication.exception.CommunicationException;
import org.athena.framework.communication.wecom.properties.WeComCommunicationProperties;
import org.arthena.framework.common.utils.JacksonJsonUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 企业微信文本消息发送实现。
 *
 * @author zhouzhitong
 * @since 2026/6/24
 */
@Slf4j
@RequiredArgsConstructor
public class WeComChannelDriver implements ChannelDriver {

    private static final String PROVIDER = "wecom";

    private final HttpClient httpClient;

    private final WeComCommunicationProperties properties;

    private final AtomicReference<CachedToken> cachedTokenRef = new AtomicReference<>();

    @Override
    public ChannelType channelType() {
        return ChannelType.WECOM;
    }

    @Override
    public SendResult send(SendRequest request) {
        List<String> userIds = request.getReceivers().stream()
            .filter(receiver -> receiver.getType() == null || receiver.getType() == ReceiverType.USER_ID)
            .map(Receiver::getTarget)
            .filter(StringUtils::isNotBlank)
            .distinct()
            .toList();
        if (userIds.isEmpty()) {
            throw new CommunicationException(CommunicationErrorCode.COMMUNICATION_INVALID_REQUEST,
                "wecom-user-empty");
        }
        try {
            String accessToken = getAccessToken();
            HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(normalizeBaseUrl() + "/cgi-bin/message/send?access_token="
                    + URLEncoder.encode(accessToken, StandardCharsets.UTF_8)))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(buildSendBody(request, userIds)))
                .build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            JsonNode body = readBody(response.body());
            int errcode = body.path("errcode").asInt(-1);
            if (errcode != 0) {
                throw new CommunicationException(CommunicationErrorCode.COMMUNICATION_SEND_ERROR,
                    "wecom-send-failed", errcode, body.path("errmsg").asText());
            }

            SendResult result = SendResult.success(ChannelType.WECOM, PROVIDER);
            result.setMessageId(body.path("msgid").asText(null));
            result.setRequestId(request.getBizType());
            result.getAttributes().put("touser", String.join("|", userIds));
            return result;
        } catch (CommunicationException e) {
            throw e;
        } catch (Exception e) {
            throw new CommunicationException(CommunicationErrorCode.COMMUNICATION_SEND_ERROR, e,
                ChannelType.WECOM.name(), e.getMessage());
        }
    }

    private String buildSendBody(SendRequest request, List<String> userIds) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("touser", String.join("|", userIds));
        payload.put("msgtype", "text");
        payload.put("agentid", properties.getAgentId());

        Map<String, Object> text = new LinkedHashMap<>();
        text.put("content", buildContent(request));
        payload.put("text", text);
        payload.put("safe", 0);
        return JacksonJsonUtils.writeValueAsString(payload);
    }

    private String buildContent(SendRequest request) {
        if (StringUtils.isBlank(request.getTitle())) {
            return request.getContent();
        }
        return request.getTitle() + "\n" + StringUtils.defaultString(request.getContent());
    }

    private String getAccessToken() throws IOException, InterruptedException {
        CachedToken current = cachedTokenRef.get();
        long now = System.currentTimeMillis();
        if (current != null && current.expireAtMs() > now) {
            return current.accessToken();
        }
        synchronized (cachedTokenRef) {
            current = cachedTokenRef.get();
            now = System.currentTimeMillis();
            if (current != null && current.expireAtMs() > now) {
                return current.accessToken();
            }
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(normalizeBaseUrl() + "/cgi-bin/gettoken?corpid="
                    + URLEncoder.encode(properties.getCorpId(), StandardCharsets.UTF_8)
                    + "&corpsecret=" + URLEncoder.encode(properties.getCorpSecret(), StandardCharsets.UTF_8)))
                .GET()
                .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode body = readBody(response.body());
            int errcode = body.path("errcode").asInt(-1);
            if (errcode != 0) {
                throw new CommunicationException(CommunicationErrorCode.COMMUNICATION_CONFIG_ERROR,
                    "wecom-token-failed", errcode, body.path("errmsg").asText());
            }
            String accessToken = body.path("access_token").asText();
            long expiresIn = body.path("expires_in").asLong(7200);
            CachedToken refreshed = new CachedToken(accessToken,
                System.currentTimeMillis() + Math.max(60, expiresIn - 60) * 1000);
            cachedTokenRef.set(refreshed);
            return refreshed.accessToken();
        }
    }

    private JsonNode readBody(String responseBody) throws IOException {
        return JacksonJsonUtils.JSON.readTree(responseBody);
    }

    private String normalizeBaseUrl() {
        return StringUtils.removeEnd(properties.getBaseUrl(), "/");
    }

    private record CachedToken(String accessToken, long expireAtMs) {
    }
}
