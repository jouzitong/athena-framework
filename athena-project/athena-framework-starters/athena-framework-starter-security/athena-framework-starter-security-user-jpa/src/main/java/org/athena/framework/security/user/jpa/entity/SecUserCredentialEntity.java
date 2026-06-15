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
 * 用户凭据实体。
 * 对应 sec_user_credential 表，保存密码哈希、算法与凭据类型等认证数据。
 */
@Getter
@Setter
@Entity
@Table(name = "sec_user_credential")
@Comment("用户凭据表")
public class SecUserCredentialEntity {

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
     * 凭据类型。
     */
    @Column(name = "credential_type", nullable = false, length = 32)
    @Comment("凭据类型")
    private String credentialType;

    /**
     * 密码哈希值。
     */
    @Column(name = "password_hash", nullable = false, length = 255)
    @Comment("密码哈希值")
    private String passwordHash;

    /**
     * 密码算法。
     */
    @Column(name = "password_algo", length = 32)
    @Comment("密码算法")
    private String passwordAlgo;

    /**
     * 密码盐值。
     */
    @Column(name = "password_salt", length = 255)
    @Comment("密码盐值")
    private String passwordSalt;
}
