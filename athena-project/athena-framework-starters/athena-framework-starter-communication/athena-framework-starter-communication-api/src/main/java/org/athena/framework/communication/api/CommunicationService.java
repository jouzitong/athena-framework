package org.athena.framework.communication.api;

/**
 * 通信统一入口。
 *
 * @author zhouzhitong
 * @since 2026/6/24
 */
public interface CommunicationService {

    SendResult send(SendRequest request);
}
