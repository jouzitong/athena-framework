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
 * 用户角色关联实体。
 */
@Getter
@Setter
@Entity
@Table(name = "sec_user_role")
@Comment("用户角色关联表")
public class SecUserRoleEntity {

    /**
     * 主键ID。
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("主键ID")
    private Long id;

    /**
     * 用户主键ID。
     */
    @Column(name = "user_id", nullable = false)
    @Comment("用户主键ID")
    private Long userId;

    /**
     * 角色编码。
     */
    @Column(name = "role_code", nullable = false, length = 64)
    @Comment("角色编码")
    private String roleCode;
}
