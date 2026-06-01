package org.athena.framework.kafka.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.athena.framework.kafka.properties.KafkaProperties;
import org.athena.framework.kafka.consumer.DynamicKafkaConsumerManager;
import org.athena.framework.kafka.consumer.impl.DefaultDynamicKafkaConsumerManager;
import org.athena.framework.kafka.publisher.MessagePublisher;
import org.athena.framework.kafka.publisher.impl.KafkaMessagePublisher;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhouzhitong
 * @since 2026/6/1
 */
@Slf4j
@AutoConfiguration
@EnableKafka
@ConditionalOnClass(KafkaTemplate.class)
@ConditionalOnProperty(prefix = "athena.kafka", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(KafkaProperties.class)
public class KafkaAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ProducerFactory<String, Object> kafkaProducerFactory(KafkaProperties properties) {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getBootstrapServers());
        config.put(ProducerConfig.ACKS_CONFIG, properties.getProducerAcks());
        config.put(ProducerConfig.RETRIES_CONFIG, properties.getProducerRetries());
        config.put(ProducerConfig.BATCH_SIZE_CONFIG, properties.getProducerBatchSize());
        config.put(ProducerConfig.LINGER_MS_CONFIG, properties.getProducerLingerMs());
        config.put(ProducerConfig.BUFFER_MEMORY_CONFIG, properties.getProducerBufferMemory());
        config.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, properties.getProducerRequestTimeoutMs());
        config.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, properties.getProducerDeliveryTimeoutMs());
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        if (properties.getClientId() != null && !properties.getClientId().isBlank()) {
            config.put(ProducerConfig.CLIENT_ID_CONFIG, properties.getClientId());
        }

        LOGGER.info("kafka producer 自动配置加载完成, bootstrapServers={}, acks={}",
            properties.getBootstrapServers(), properties.getProducerAcks());
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    @ConditionalOnMissingBean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public MessagePublisher messagePublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        return new KafkaMessagePublisher(kafkaTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    public DynamicKafkaConsumerManager dynamicKafkaConsumerManager(ConsumerFactory<String, Object> consumerFactory) {
        return new DefaultDynamicKafkaConsumerManager(consumerFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public ConsumerFactory<String, Object> kafkaConsumerFactory(KafkaProperties properties) {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getBootstrapServers());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, properties.getConsumerGroupId());
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, properties.getConsumerAutoOffsetReset());
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, properties.isConsumerEnableAutoCommit());
        config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, properties.getConsumerMaxPollRecords());
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        if (properties.getClientId() != null && !properties.getClientId().isBlank()) {
            config.put(ConsumerConfig.CLIENT_ID_CONFIG, properties.getClientId());
        }

        LOGGER.info("kafka consumer 自动配置加载完成, bootstrapServers={}, groupId={}, autoCommit={}",
            properties.getBootstrapServers(), properties.getConsumerGroupId(), properties.isConsumerEnableAutoCommit());
        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean(name = "kafkaListenerContainerFactory")
    @ConditionalOnMissingBean(name = "kafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
        ConsumerFactory<String, Object> consumerFactory,
        KafkaProperties properties) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(properties.getListenerConcurrency());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }
}
