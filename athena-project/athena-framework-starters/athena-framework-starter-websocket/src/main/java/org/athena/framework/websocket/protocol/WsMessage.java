package org.athena.framework.websocket.protocol;

/**
 * 通用协议消息
 */
public class WsMessage {

    /**
     * 协议版本
     */
    private String version;
    /**
     * 消息类型
     */
    private String type;
    /**
     * 请求关联 ID
     */
    private String requestId;
    /**
     * 业务 topic
     */
    private String topic;
    /**
     * 业务负载
     */
    private Object payload;
    /**
     * 毫秒时间戳
     */
    private long timestamp;
    /**
     * 元信息（traceId/clientId/resumeId 等）
     */
    private WsMeta meta;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public WsMeta getMeta() {
        return meta;
    }

    public void setMeta(WsMeta meta) {
        this.meta = meta;
    }
}
