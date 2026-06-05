package org.athena.framework.security.user.mybatis.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SecUserCredentialEntity {
    private Long id;
    private Long userId;
    private String credentialType;
    private String passwordHash;
    private String passwordAlgo;
    private String passwordSalt;
}
