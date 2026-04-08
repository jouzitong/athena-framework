package org.athena.framework.security.user.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "sec_user_credential")
/**
 * 用户凭据实体。
 * 对应 sec_user_credential 表，保存密码哈希、算法与凭据类型等认证数据。
 */
public class SecUserCredentialEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, length = 64)
    private String userId;

    @Column(name = "credential_type", nullable = false, length = 32)
    private String credentialType;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "password_algo", length = 32)
    private String passwordAlgo;

    @Column(name = "password_salt", length = 255)
    private String passwordSalt;
}
