package org.athena.framework.kafka.properties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * @author zhouzhitong
 * @since 2026/6/1
 */
@Data
@Validated
@ConfigurationProperties(prefix = "athena.kafka")
public class KafkaProperties {

    private boolean enabled = false;

    @NotBlank(message = "athena.kafka.bootstrap-servers 不能为空")
    private String bootstrapServers;

    private String clientId;

    private String producerAcks = "all";

    @Min(value = 0, message = "athena.kafka.producer-retries 不能小于 0")
    private int producerRetries = 3;

    @Min(value = 0, message = "athena.kafka.producer-batch-size 不能小于 0")
    private int producerBatchSize = 16384;

    @Min(value = 0, message = "athena.kafka.producer-linger-ms 不能小于 0")
    private int producerLingerMs = 5;

    @Min(value = 1, message = "athena.kafka.producer-buffer-memory 需大于 0")
    private long producerBufferMemory = 33554432L;

    @Min(value = 1, message = "athena.kafka.producer-request-timeout-ms 需大于 0")
    private int producerRequestTimeoutMs = 30000;

    @Min(value = 1, message = "athena.kafka.producer-delivery-timeout-ms 需大于 0")
    private int producerDeliveryTimeoutMs = 120000;

    @NotBlank(message = "athena.kafka.consumer-group-id 不能为空")
    private String consumerGroupId;

    private String consumerAutoOffsetReset = "latest";

    private boolean consumerEnableAutoCommit = false;

    @Min(value = 1, message = "athena.kafka.consumer-max-poll-records 需大于 0")
    private int consumerMaxPollRecords = 500;

    @Min(value = 1, message = "athena.kafka.listener-concurrency 需大于 0")
    private int listenerConcurrency = 1;
}
