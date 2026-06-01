package org.athena.framework.kafka.publisher.impl;

import lombok.RequiredArgsConstructor;
import org.athena.framework.kafka.publisher.MessagePublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author zhouzhitong
 * @since 2026/6/1
 */
@RequiredArgsConstructor
public class KafkaMessagePublisher implements MessagePublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public CompletableFuture<SendResult<String, Object>> send(String topic, Object payload) {
        return kafkaTemplate.send(topic, payload);
    }

    @Override
    public CompletableFuture<SendResult<String, Object>> send(String topic, String key, Object payload) {
        return kafkaTemplate.send(topic, key, payload);
    }

    @Override
    public CompletableFuture<SendResult<String, Object>> send(String topic, String key, Map<String, Object> headers,
                                                               Object payload) {
        MessageBuilder<Object> builder = MessageBuilder.withPayload(payload)
            .setHeader(KafkaHeaders.TOPIC, topic)
            .setHeader(KafkaHeaders.KEY, key);

        if (headers != null && !headers.isEmpty()) {
            headers.forEach(builder::setHeader);
        }

        Message<Object> message = builder.build();
        return kafkaTemplate.send(message);
    }
}
