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
 * 角色实体。
 */
@Getter
@Setter
@Entity
@Table(name = "sec_role")
@Comment("角色表")
public class SecRoleEntity {

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
    @Column(name = "role_code", nullable = false, unique = true, length = 64)
    @Comment("角色编码")
    private String roleCode;

    /**
     * 角色名称。
     */
    @Column(name = "role_name", nullable = false, length = 128)
    @Comment("角色名称")
    private String roleName;

    /**
     * 角色状态。
     */
    @Column(name = "status", nullable = false, length = 16)
    @Comment("角色状态")
    private String status;
}
