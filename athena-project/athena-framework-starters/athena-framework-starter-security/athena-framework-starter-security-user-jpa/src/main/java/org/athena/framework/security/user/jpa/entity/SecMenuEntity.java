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
@Table(name = "sec_menu")
public class SecMenuEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "menu_code", nullable = false, unique = true, length = 64)
    private String menuCode;

    @Column(name = "parent_code", length = 64)
    private String parentCode;

    @Column(name = "menu_name", nullable = false, length = 128)
    private String menuName;

    @Column(name = "path", length = 255)
    private String path;

    @Column(name = "component", length = 255)
    private String component;

    @Column(name = "permission_code", length = 128)
    private String permissionCode;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "status", nullable = false, length = 16)
    private String status;
}
