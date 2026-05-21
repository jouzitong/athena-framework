package org.athena.framework.security.user.mybatis.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SecRoleEntity {
    private Long id;
    private String roleCode;
    private String roleName;
    private String status;
}
