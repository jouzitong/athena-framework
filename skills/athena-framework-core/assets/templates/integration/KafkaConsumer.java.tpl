package {{PACKAGE}}.messaging;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.athena.framework.kafka.consumer.DynamicKafkaConsumerHandler;
import org.springframework.kafka.support.Acknowledgment;

public class {{NAME}}EventHandler implements DynamicKafkaConsumerHandler {

    private final EventPort eventPort;

    public {{NAME}}EventHandler(EventPort eventPort) {
        this.eventPort = eventPort;
    }

    @Override
    public void onMessage(ConsumerRecord<String, Object> record, Acknowledgment acknowledgment) {
        eventPort.handle(record.key(), record.value());
        acknowledgment.acknowledge();
    }

    @FunctionalInterface
    public interface EventPort {
        void handle(String key, Object event);
    }
}
