package org.athena.framework.security.user.mybatis.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SecMenuEntity {
    private Long id;
    private String menuCode;
    private String parentCode;
    private String menuName;
    private String path;
    private String component;
    private String permissionCode;
    private Integer sortOrder;
    private String status;
}
