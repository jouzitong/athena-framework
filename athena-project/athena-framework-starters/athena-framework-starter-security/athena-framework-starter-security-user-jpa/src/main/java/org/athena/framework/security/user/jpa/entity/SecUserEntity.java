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

/**
 * 用户主表实体。
 * 对应 sec_user 表，承载用户身份基础字段与状态信息。
 */
@Getter
@Setter
@Entity
@Table(name = "sec_user")
@Comment("用户主表")
public class SecUserEntity {

    /**
     * 主键ID。
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("主键ID")
    private Long id;

    /**
     * 用户唯一标识。
     */
    @Column(name = "user_id", nullable = false, unique = true, length = 64)
    @Comment("用户唯一标识")
    private String userId;

    /**
     * 登录用户名。
     */
    @Column(name = "username", nullable = false, length = 64)
    @Comment("登录用户名")
    private String username;

    /**
     * 展示名称。
     */
    @Column(name = "display_name", length = 128)
    @Comment("展示名称")
    private String displayName;

    /**
     * 用户状态。
     */
    @Column(name = "status", nullable = false, length = 16)
    @Comment("用户状态")
    private String status;

    /**
     * 租户ID。
     */
    @Column(name = "tenant_id", length = 64)
    @Comment("租户ID")
    private String tenantId;
}
