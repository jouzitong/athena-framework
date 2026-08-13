package {{PACKAGE}}.messaging;

import org.athena.framework.kafka.publisher.MessagePublisher;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class {{NAME}}EventPublisher {

    private static final String TOPIC = "{{NAME_LOWER}}.events";

    private final MessagePublisher publisher;

    public {{NAME}}EventPublisher(MessagePublisher publisher) {
        this.publisher = publisher;
    }

    public CompletableFuture<SendResult<String, Object>> publish(String key, Object event) {
        return publisher.send(TOPIC, key, event);
    }
}
