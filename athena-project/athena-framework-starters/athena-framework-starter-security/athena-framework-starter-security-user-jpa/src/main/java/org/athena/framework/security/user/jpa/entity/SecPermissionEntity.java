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
 * 权限实体。
 */
@Getter
@Setter
@Entity
@Table(name = "sec_permission")
@Comment("权限表")
public class SecPermissionEntity {

    /**
     * 主键ID。
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("主键ID")
    private Long id;

    /**
     * 权限编码。
     */
    @Column(name = "permission_code", nullable = false, unique = true, length = 128)
    @Comment("权限编码")
    private String permissionCode;

    /**
     * 权限名称。
     */
    @Column(name = "permission_name", nullable = false, length = 128)
    @Comment("权限名称")
    private String permissionName;

    /**
     * 权限状态。
     */
    @Column(name = "status", nullable = false, length = 16)
    @Comment("权限状态")
    private String status;
}
