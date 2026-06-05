package org.athena.framework.security.user.mybatis.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SecUserEntity {
    private Long id;
    private String username;
    private String displayName;
    private String status;
    private String tenantId;
}
