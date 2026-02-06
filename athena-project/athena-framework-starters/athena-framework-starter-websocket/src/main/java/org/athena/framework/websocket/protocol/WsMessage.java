package org.athena.framework.websocket.protocol;

public class WsMessage {

    private String version;
    private String type;
    private String requestId;
    private String topic;
    private Object payload;
    private long timestamp;
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
