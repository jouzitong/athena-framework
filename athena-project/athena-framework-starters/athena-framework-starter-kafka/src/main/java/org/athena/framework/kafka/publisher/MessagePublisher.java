package org.athena.framework.kafka.publisher;

import org.springframework.kafka.support.SendResult;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Kafka 消息发布抽象，业务只关心 topic/key/payload。
 *
 * @author zhouzhitong
 * @since 2026/6/1
 */
public interface MessagePublisher {

    CompletableFuture<SendResult<String, Object>> send(String topic, Object payload);

    CompletableFuture<SendResult<String, Object>> send(String topic, String key, Object payload);

    CompletableFuture<SendResult<String, Object>> send(String topic, String key, Map<String, Object> headers,
                                                       Object payload);
}
