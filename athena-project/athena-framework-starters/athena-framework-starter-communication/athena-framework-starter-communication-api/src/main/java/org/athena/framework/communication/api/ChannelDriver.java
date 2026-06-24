package org.athena.framework.communication.api;

/**
 * 具体通信渠道驱动。
 *
 * @author zhouzhitong
 * @since 2026/6/24
 */
public interface ChannelDriver {

    ChannelType channelType();

    SendResult send(SendRequest request);

    default boolean supports(SendRequest request) {
        return request != null && request.getChannel() == channelType();
    }
}
