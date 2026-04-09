package org.athena.framework.security.api.model;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 审计记录模型。
 * 用于在安全事件和持久化适配器之间传递标准化审计数据。
 */
public class AuditRecord {

    private String category;

    private String action;

    private String result;

    private String userId;

    private String username;

    private String tenantId;

    private String resource;

    private String detail;

    private String requestIp;

    private Instant occurredAt;

    private final Map<String, Object> attributes = new LinkedHashMap<>();

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getRequestIp() {
        return requestIp;
    }

    public void setRequestIp(String requestIp) {
        this.requestIp = requestIp;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }
}
