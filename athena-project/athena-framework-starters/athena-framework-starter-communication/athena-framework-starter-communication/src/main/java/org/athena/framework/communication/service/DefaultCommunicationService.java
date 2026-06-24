package org.athena.framework.communication.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.athena.framework.communication.api.ChannelDriver;
import org.athena.framework.communication.api.ChannelType;
import org.athena.framework.communication.api.CommunicationService;
import org.athena.framework.communication.api.SendRequest;
import org.athena.framework.communication.api.SendResult;
import org.athena.framework.communication.config.CommunicationProperties;
import org.athena.framework.communication.constant.CommunicationErrorCode;
import org.athena.framework.communication.exception.CommunicationException;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 通信统一路由实现。
 *
 * @author zhouzhitong
 * @since 2026/6/24
 */
@Slf4j
public class DefaultCommunicationService implements CommunicationService {

    private final Map<ChannelType, ChannelDriver> driverMap;

    private final CommunicationProperties properties;

    public DefaultCommunicationService(List<ChannelDriver> drivers, CommunicationProperties properties) {
        this.properties = properties;
        this.driverMap = new EnumMap<>(ChannelType.class);
        for (ChannelDriver driver : drivers) {
            ChannelDriver previous = driverMap.putIfAbsent(driver.channelType(), driver);
            if (previous != null) {
                throw new CommunicationException(CommunicationErrorCode.COMMUNICATION_CONFIG_ERROR,
                    "duplicate-driver", driver.channelType().name());
            }
        }
    }

    @Override
    public SendResult send(SendRequest request) {
        validate(request);
        ChannelDriver driver = driverMap.get(request.getChannel());
        if (driver == null || !driver.supports(request)) {
            throw new CommunicationException(CommunicationErrorCode.COMMUNICATION_CHANNEL_NOT_FOUND,
                request.getChannel().name());
        }
        LOGGER.info("communication send start, channel={}, bizType={}, receivers={}",
            request.getChannel(), request.getBizType(), request.getReceivers().size());
        return driver.send(request);
    }

    private void validate(SendRequest request) {
        if (!properties.isEnabled()) {
            throw new CommunicationException(CommunicationErrorCode.COMMUNICATION_CONFIG_ERROR, "communication-disabled");
        }
        if (request == null) {
            throw new CommunicationException(CommunicationErrorCode.COMMUNICATION_INVALID_REQUEST, "request-null");
        }
        if (request.getChannel() == null) {
            throw new CommunicationException(CommunicationErrorCode.COMMUNICATION_INVALID_REQUEST, "channel-empty");
        }
        if (request.getReceivers() == null || request.getReceivers().isEmpty()) {
            throw new CommunicationException(CommunicationErrorCode.COMMUNICATION_INVALID_REQUEST, "receivers-empty");
        }
        if (!properties.isAllowDirectContent() && StringUtils.isBlank(request.getTemplateCode())) {
            throw new CommunicationException(CommunicationErrorCode.COMMUNICATION_INVALID_REQUEST,
                "templateCode-empty");
        }
        if (properties.isAllowDirectContent()
            && StringUtils.isBlank(request.getTemplateCode())
            && StringUtils.isBlank(request.getContent())) {
            throw new CommunicationException(CommunicationErrorCode.COMMUNICATION_INVALID_REQUEST, "content-empty");
        }
    }
}
