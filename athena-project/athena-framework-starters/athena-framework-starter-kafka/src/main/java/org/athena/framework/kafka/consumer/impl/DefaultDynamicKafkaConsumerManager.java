package org.athena.framework.kafka.consumer.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.athena.framework.kafka.consumer.DynamicKafkaConsumerHandler;
import org.athena.framework.kafka.consumer.DynamicKafkaConsumerManager;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import jakarta.annotation.PreDestroy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhouzhitong
 * @since 2026/6/1
 */
@Slf4j
public class DefaultDynamicKafkaConsumerManager implements DynamicKafkaConsumerManager {

    private final ConsumerFactory<String, Object> consumerFactory;

    private final Map<String, ConcurrentMessageListenerContainer<String, Object>> containerMap =
        new ConcurrentHashMap<>();

    public DefaultDynamicKafkaConsumerManager(ConsumerFactory<String, Object> consumerFactory) {
        this.consumerFactory = consumerFactory;
    }

    @Override
    public void createAndStart(String consumerId, String topic, String groupId, int concurrency,
                               DynamicKafkaConsumerHandler handler) {
        validate(consumerId, topic, groupId, concurrency, handler);

        ConcurrentMessageListenerContainer<String, Object> container = containerMap.compute(consumerId,
            (id, oldContainer) -> {
                if (oldContainer != null) {
                    stopContainer(id, oldContainer);
                }
                return buildContainer(id, topic, groupId, concurrency, handler);
            });

        container.start();
        LOGGER.info("dynamic kafka consumer started, consumerId={}, topic={}, groupId={}, concurrency={}",
            consumerId, topic, groupId, concurrency);
    }

    @Override
    public void stop(String consumerId) {
        ConcurrentMessageListenerContainer<String, Object> container = containerMap.remove(consumerId);
        if (container != null) {
            stopContainer(consumerId, container);
        }
    }

    @Override
    public boolean isRunning(String consumerId) {
        ConcurrentMessageListenerContainer<String, Object> container = containerMap.get(consumerId);
        return container != null && container.isRunning();
    }

    @Override
    public Collection<String> listConsumerIds() {
        return new ArrayList<>(containerMap.keySet());
    }

    private ConcurrentMessageListenerContainer<String, Object> buildContainer(String consumerId,
                                                                               String topic,
                                                                               String groupId,
                                                                               int concurrency,
                                                                               DynamicKafkaConsumerHandler handler) {
        ContainerProperties containerProperties = new ContainerProperties(topic);
        containerProperties.setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        containerProperties.setGroupId(groupId);
        containerProperties.setMessageListener((org.springframework.kafka.listener.AcknowledgingMessageListener<String, Object>)
            (record, acknowledgment) -> handler.onMessage(record, acknowledgment));

        Map<String, Object> overrides = new HashMap<>();
        overrides.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        ConcurrentMessageListenerContainer<String, Object> container =
            new ConcurrentMessageListenerContainer<>(consumerFactory, containerProperties);
        container.setBeanName("athenaDynamicKafkaConsumer-" + consumerId);
        container.setConcurrency(concurrency);
        container.getContainerProperties().getKafkaConsumerProperties().putAll(overrides);
        return container;
    }

    @PreDestroy
    public void shutdown() {
        containerMap.forEach(this::stopContainer);
        containerMap.clear();
    }

    private void stopContainer(String consumerId, ConcurrentMessageListenerContainer<String, Object> container) {
        try {
            container.stop();
            LOGGER.info("dynamic kafka consumer stopped, consumerId={}", consumerId);
        } catch (Exception ex) {
            LOGGER.warn("dynamic kafka consumer stop failed, consumerId={}", consumerId, ex);
        }
    }

    private void validate(String consumerId, String topic, String groupId, int concurrency,
                          DynamicKafkaConsumerHandler handler) {
        if (StringUtils.isBlank(consumerId)) {
            throw new IllegalArgumentException("consumerId 不能为空");
        }
        if (StringUtils.isBlank(topic)) {
            throw new IllegalArgumentException("topic 不能为空");
        }
        if (StringUtils.isBlank(groupId)) {
            throw new IllegalArgumentException("groupId 不能为空");
        }
        if (concurrency < 1) {
            throw new IllegalArgumentException("concurrency 必须大于 0");
        }
        if (handler == null) {
            throw new IllegalArgumentException("handler 不能为空");
        }
    }
}
