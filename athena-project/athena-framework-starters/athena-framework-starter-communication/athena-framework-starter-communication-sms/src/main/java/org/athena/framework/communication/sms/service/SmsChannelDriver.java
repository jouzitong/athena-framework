package org.athena.framework.communication.sms.service;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.dysmsapi20170525.models.SendSmsResponseBody;
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
import org.athena.framework.communication.sms.properties.SmsCommunicationProperties;
import org.arthena.framework.common.utils.JacksonJsonUtils;

import java.util.List;
import java.util.Map;

/**
 * 阿里云短信发送实现。
 *
 * @author zhouzhitong
 * @since 2026/6/29
 */
@Slf4j
@RequiredArgsConstructor
public class SmsChannelDriver implements ChannelDriver {

    private static final String PROVIDER = "aliyun-dysmsapi";

    private final Client client;

    private final SmsCommunicationProperties properties;

    @Override
    public ChannelType channelType() {
        return ChannelType.SMS;
    }

    @Override
    public SendResult send(SendRequest request) {
        List<String> phoneNumbers = request.getReceivers().stream()
            .filter(receiver -> receiver.getType() == null || receiver.getType() == ReceiverType.PHONE)
            .map(Receiver::getTarget)
            .filter(StringUtils::isNotBlank)
            .distinct()
            .toList();
        if (phoneNumbers.isEmpty()) {
            throw new CommunicationException(CommunicationErrorCode.COMMUNICATION_INVALID_REQUEST,
                "sms-phone-empty");
        }
        if (StringUtils.isBlank(request.getTemplateCode())) {
            throw new CommunicationException(CommunicationErrorCode.COMMUNICATION_INVALID_REQUEST,
                "sms-templateCode-empty");
        }
        String signName = resolveAttribute(request, "signName", properties.getSignName());
        if (StringUtils.isBlank(signName)) {
            throw new CommunicationException(CommunicationErrorCode.COMMUNICATION_INVALID_REQUEST,
                "sms-signName-empty");
        }
        try {
            SendSmsRequest smsRequest = new SendSmsRequest()
                .setPhoneNumbers(String.join(",", phoneNumbers))
                .setSignName(signName)
                .setTemplateCode(request.getTemplateCode());
            String outId = resolveAttribute(request, "outId", request.getBizType());
            if (StringUtils.isNotBlank(outId)) {
                smsRequest.setOutId(outId);
            }
            String smsUpExtendCode = resolveAttribute(request, "smsUpExtendCode", properties.getSmsUpExtendCode());
            if (StringUtils.isNotBlank(smsUpExtendCode)) {
                smsRequest.setSmsUpExtendCode(smsUpExtendCode);
            }
            if (request.getTemplateParams() != null && !request.getTemplateParams().isEmpty()) {
                smsRequest.setTemplateParam(JacksonJsonUtils.writeValueAsString(request.getTemplateParams()));
            }
            SendSmsResponse response = client.sendSms(smsRequest);
            SendSmsResponseBody body = response.getBody();
            if (body == null) {
                throw new CommunicationException(CommunicationErrorCode.COMMUNICATION_SEND_ERROR,
                    "aliyun-sms-empty-response");
            }
            if (!StringUtils.equalsIgnoreCase("OK", body.getCode())) {
                throw new CommunicationException(CommunicationErrorCode.COMMUNICATION_SEND_ERROR,
                    "aliyun-sms-send-failed", body.getCode(), body.getMessage());
            }

            SendResult result = SendResult.success(ChannelType.SMS, PROVIDER);
            result.setRequestId(body.getRequestId());
            result.setMessageId(body.getBizId());
            result.getAttributes().put("phoneNumbers", phoneNumbers);
            if (StringUtils.isNotBlank(outId)) {
                result.getAttributes().put("outId", outId);
            }
            return result;
        } catch (CommunicationException e) {
            throw e;
        } catch (Exception e) {
            throw new CommunicationException(CommunicationErrorCode.COMMUNICATION_SEND_ERROR, e,
                ChannelType.SMS.name(), e.getMessage());
        }
    }

    private String resolveAttribute(SendRequest request, String attributeName, String defaultValue) {
        Map<String, Object> attributes = request.getAttributes();
        if (attributes == null) {
            return defaultValue;
        }
        Object value = attributes.get(attributeName);
        if (value instanceof String text && StringUtils.isNotBlank(text)) {
            return text;
        }
        return defaultValue;
    }
}
