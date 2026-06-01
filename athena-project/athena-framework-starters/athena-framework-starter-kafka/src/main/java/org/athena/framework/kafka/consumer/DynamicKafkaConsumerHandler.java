package org.athena.framework.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.support.Acknowledgment;

/**
 * 动态消费者处理器，业务方负责处理并决定何时 ack。
 *
 * @author zhouzhitong
 * @since 2026/6/1
 */
@FunctionalInterface
public interface DynamicKafkaConsumerHandler {

    void onMessage(ConsumerRecord<String, Object> record, Acknowledgment acknowledgment);
}
