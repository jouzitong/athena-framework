package org.athena.framework.websocket.protocol;

/**
 * 通用元信息
 */
public class WsMeta {

    /**
     * 追踪 ID
     */
    private String traceId;
    /**
     * 客户端稳定标识
     */
    private String clientId;
    /**
     * 会话恢复 ID
     */
    private String resumeId;
    /**
     * 压缩方式
     */
    private String compress;
    /**
     * 优先级
     */
    private Integer priority;

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getResumeId() {
        return resumeId;
    }

    public void setResumeId(String resumeId) {
        this.resumeId = resumeId;
    }

    public String getCompress() {
        return compress;
    }

    public void setCompress(String compress) {
        this.compress = compress;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
