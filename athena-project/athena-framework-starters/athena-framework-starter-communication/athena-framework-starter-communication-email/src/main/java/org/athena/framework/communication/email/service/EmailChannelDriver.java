package org.athena.framework.communication.email.service;

import jakarta.mail.internet.MimeMessage;
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
import org.athena.framework.communication.email.properties.EmailCommunicationProperties;
import org.athena.framework.communication.exception.CommunicationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 邮件渠道发送实现。
 *
 * @author zhouzhitong
 * @since 2026/6/24
 */
@Slf4j
@RequiredArgsConstructor
public class EmailChannelDriver implements ChannelDriver {

    private static final String PROVIDER = "spring-mail";

    private final JavaMailSender mailSender;

    private final EmailCommunicationProperties properties;

    @Override
    public ChannelType channelType() {
        return ChannelType.EMAIL;
    }

    @Override
    public SendResult send(SendRequest request) {
        List<String> recipients = request.getReceivers().stream()
            .filter(receiver -> receiver.getType() == null || receiver.getType() == ReceiverType.EMAIL)
            .map(Receiver::getTarget)
            .filter(StringUtils::isNotBlank)
            .distinct()
            .toList();
        if (recipients.isEmpty()) {
            throw new CommunicationException(CommunicationErrorCode.COMMUNICATION_INVALID_REQUEST,
                "email-receivers-empty");
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());
            if (StringUtils.isNotBlank(properties.getFrom())) {
                helper.setFrom(properties.getFrom());
            }
            helper.setTo(recipients.toArray(String[]::new));
            helper.setSubject(buildSubject(request));
            helper.setText(StringUtils.defaultString(request.getContent()), resolveHtml(request));
            mailSender.send(message);

            SendResult result = SendResult.success(ChannelType.EMAIL, PROVIDER);
            result.setRequestId(request.getBizType());
            result.getAttributes().put("recipients", recipients.size());
            return result;
        } catch (Exception e) {
            throw new CommunicationException(CommunicationErrorCode.COMMUNICATION_SEND_ERROR, e,
                ChannelType.EMAIL.name(), e.getMessage());
        }
    }

    private String buildSubject(SendRequest request) {
        if (StringUtils.isNotBlank(request.getTitle())) {
            return request.getTitle();
        }
        if (StringUtils.isNotBlank(request.getBizType())) {
            return request.getBizType();
        }
        return "Athena Notification";
    }

    private boolean resolveHtml(SendRequest request) {
        Object html = request.getAttributes().get("html");
        if (html instanceof Boolean value) {
            return value;
        }
        return properties.isHtml();
    }
}
