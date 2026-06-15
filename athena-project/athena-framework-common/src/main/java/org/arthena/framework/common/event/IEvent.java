package org.arthena.framework.common.event;

import java.io.Serializable;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;

/**
 * 基本事件
 *
 * @author zhouzhitong
 * @since 2023/2/13
 */
public interface IEvent extends Serializable {

    /**
     * 事件ID
     */
    String eventId();

    /**
     * 事件发生时间
     */
    Instant occurredAt();

    /**
     * 事件来源，默认unknown
     */
    String source();

    /**
     * MQ 主题（Kafka topic / Redis channel）
     */
    String topic();

    /**
     * 分区或路由 key
     */
    default String key() {
        return eventId();
    }

    /**
     * 可扩展消息头
     */
    default Map<String, String> headers() {
        return Collections.emptyMap();
    }

    /**
     * 消息体（需可序列化）
     */
    Object payload();
}
