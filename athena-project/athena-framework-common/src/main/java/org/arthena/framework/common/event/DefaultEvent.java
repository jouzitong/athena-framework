package org.arthena.framework.common.event;

import lombok.Getter;
import lombok.ToString;
import org.arthena.framework.common.utils.SystemUtils;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

/**
 * 默认事件基类
 *
 * @author zhouzhitong
 */
@Getter
@ToString
public class DefaultEvent implements IEvent {

    private final String eventId;
    private final Instant occurredAt;
    private final String source;
    private final String topic;
    private final String key;
    private final Object payload;
    private final Map<String, String> headers;

    private DefaultEvent(String source, String topic, String key, Object payload, Map<String, String> headers) {
        this.eventId = UUID.randomUUID().toString();
        this.occurredAt = Instant.now();
        this.source = source;
        this.topic = topic;
        this.key = key;
        this.payload = payload;
        this.headers = headers == null ? Map.of() : Collections.unmodifiableMap(headers);
    }

    public static DefaultEvent of(String topic, String key, Object payload) {
        return new DefaultEvent(SystemUtils.resolveServiceName(), topic, key, payload, Map.of());
    }

    public static DefaultEvent of(String topic, Object payload) {
        return new DefaultEvent(SystemUtils.resolveServiceName(), topic, UUID.randomUUID().toString(), payload, Map.of());
    }

    public static DefaultEvent of(String topic, String key, Object payload, Map<String, String> headers) {
        return new DefaultEvent(SystemUtils.resolveServiceName(), topic, key, payload, headers);
    }

    public static DefaultEvent of(String source, String topic, String key, Object payload, Map<String, String> headers) {
        return new DefaultEvent(source, topic, key, payload, headers);
    }

    @Override
    public String eventId() {
        return eventId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    @Override
    public String source() {
        return source;
    }

    @Override
    public String topic() {
        return topic;
    }

    @Override
    public Object payload() {
        return payload;
    }
}
