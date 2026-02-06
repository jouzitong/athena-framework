package org.athena.framework.websocket.protocol;

/**
 * 业务载荷字段常量
 */
public interface WsPayloadKey {
    String STATUS = "status";
    String CODE = "code";
    String MESSAGE = "message";
    String RETRYABLE = "retryable";
    String DATA = "data";
    String SUBSCRIBED = "subscribed";
    String RESTORED_TOPICS = "restoredTopics";
    String ACTION = "action";
    String RESUME_ID = "resumeId";
}
