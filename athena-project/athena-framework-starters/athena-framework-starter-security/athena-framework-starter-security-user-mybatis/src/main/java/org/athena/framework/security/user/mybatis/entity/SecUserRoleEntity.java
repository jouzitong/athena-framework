package org.athena.framework.security.user.mybatis.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SecUserRoleEntity {
    private Long id;
    private String userId;
    private String roleCode;
}
