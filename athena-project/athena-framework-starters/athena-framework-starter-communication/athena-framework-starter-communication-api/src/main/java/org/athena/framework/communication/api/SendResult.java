package org.athena.framework.communication.api;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 统一发送结果。
 *
 * @author zhouzhitong
 * @since 2026/6/24
 */
@Data
public class SendResult {

    private boolean success;

    private ChannelType channel;

    private String provider;

    private String requestId;

    private String messageId;

    private String errorCode;

    private String errorMessage;

    private Map<String, Object> attributes = new LinkedHashMap<>();

    public static SendResult success(ChannelType channel, String provider) {
        SendResult result = new SendResult();
        result.setSuccess(true);
        result.setChannel(channel);
        result.setProvider(provider);
        return result;
    }

    public static SendResult failure(ChannelType channel, String provider, String errorCode, String errorMessage) {
        SendResult result = new SendResult();
        result.setSuccess(false);
        result.setChannel(channel);
        result.setProvider(provider);
        result.setErrorCode(errorCode);
        result.setErrorMessage(errorMessage);
        return result;
    }
}
