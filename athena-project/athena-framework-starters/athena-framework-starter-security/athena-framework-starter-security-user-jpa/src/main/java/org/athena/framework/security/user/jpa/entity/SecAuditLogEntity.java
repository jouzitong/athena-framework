package org.athena.framework.security.user.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "sec_audit_log")
public class SecAuditLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category", nullable = false, length = 32)
    private String category;

    @Column(name = "action", nullable = false, length = 64)
    private String action;

    @Column(name = "result", nullable = false, length = 16)
    private String result;

    @Column(name = "user_id", length = 64)
    private String userId;

    @Column(name = "username", length = 64)
    private String username;

    @Column(name = "tenant_id", length = 64)
    private String tenantId;

    @Column(name = "resource", length = 255)
    private String resource;

    @Column(name = "detail", length = 500)
    private String detail;

    @Column(name = "request_ip", length = 64)
    private String requestIp;

    @Column(name = "attributes_json", length = 2000)
    private String attributesJson;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;
}
