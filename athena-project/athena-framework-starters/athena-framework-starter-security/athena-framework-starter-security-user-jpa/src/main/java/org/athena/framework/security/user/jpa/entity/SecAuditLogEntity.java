package org.athena.framework.security.user.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.time.Instant;

/**
 * 审计日志实体。
 */
@Getter
@Setter
@Entity
@Table(name = "sec_audit_log")
@Comment("审计日志表")
public class SecAuditLogEntity {

    /**
     * 主键ID。
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("主键ID")
    private Long id;

    /**
     * 日志分类。
     */
    @Column(name = "category", nullable = false, length = 32)
    @Comment("日志分类")
    private String category;

    /**
     * 操作动作。
     */
    @Column(name = "action", nullable = false, length = 64)
    @Comment("操作动作")
    private String action;

    /**
     * 操作结果。
     */
    @Column(name = "result", nullable = false, length = 16)
    @Comment("操作结果")
    private String result;

    /**
     * 用户唯一标识。
     */
    @Column(name = "user_id", length = 64)
    @Comment("用户唯一标识")
    private String userId;

    /**
     * 用户名。
     */
    @Column(name = "username", length = 64)
    @Comment("用户名")
    private String username;

    /**
     * 租户ID。
     */
    @Column(name = "tenant_id", length = 64)
    @Comment("租户ID")
    private String tenantId;

    /**
     * 资源标识。
     */
    @Column(name = "resource", length = 255)
    @Comment("资源标识")
    private String resource;

    /**
     * 详情描述。
     */
    @Column(name = "detail", length = 500)
    @Comment("详情描述")
    private String detail;

    /**
     * 请求IP地址。
     */
    @Column(name = "request_ip", length = 64)
    @Comment("请求IP地址")
    private String requestIp;

    /**
     * 扩展属性JSON。
     */
    @Column(name = "attributes_json", length = 2000)
    @Comment("扩展属性JSON")
    private String attributesJson;

    /**
     * 发生时间。
     */
    @Column(name = "occurred_at", nullable = false)
    @Comment("发生时间")
    private Instant occurredAt;
}
