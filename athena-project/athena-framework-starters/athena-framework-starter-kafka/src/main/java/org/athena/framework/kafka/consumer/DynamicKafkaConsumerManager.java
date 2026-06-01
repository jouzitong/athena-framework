package org.athena.framework.kafka.consumer;

import java.util.Collection;

/**
 * 运行时动态消费者管理。
 *
 * @author zhouzhitong
 * @since 2026/6/1
 */
public interface DynamicKafkaConsumerManager {

    void createAndStart(String consumerId, String topic, String groupId, int concurrency,
                        DynamicKafkaConsumerHandler handler);

    void stop(String consumerId);

    boolean isRunning(String consumerId);

    Collection<String> listConsumerIds();
}
