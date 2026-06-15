package org.athena.framework.security.user.mybatis.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class SecAuditLogEntity {
    private Long id;
    private String category;
    private String action;
    private String result;
    private Long userId;
    private String username;
    private String tenantId;
    private String resource;
    private String detail;
    private String requestIp;
    private String attributesJson;
    private Instant occurredAt;
}
