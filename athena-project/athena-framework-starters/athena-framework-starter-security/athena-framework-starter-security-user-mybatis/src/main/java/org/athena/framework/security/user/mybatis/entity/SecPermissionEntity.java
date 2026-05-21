package org.athena.framework.security.user.mybatis.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SecPermissionEntity {
    private Long id;
    private String permissionCode;
    private String permissionName;
    private String status;
}
