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
 * 角色权限关联实体。
 */
@Getter
@Setter
@Entity
@Table(name = "sec_role_permission")
@Comment("角色权限关联表")
public class SecRolePermissionEntity {

    /**
     * 主键ID。
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("主键ID")
    private Long id;

    /**
     * 角色编码。
     */
    @Column(name = "role_code", nullable = false, length = 64)
    @Comment("角色编码")
    private String roleCode;

    /**
     * 权限编码。
     */
    @Column(name = "permission_code", nullable = false, length = 128)
    @Comment("权限编码")
    private String permissionCode;
}
