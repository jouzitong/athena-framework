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
 * 菜单实体。
 */
@Getter
@Setter
@Entity
@Table(name = "sec_menu")
@Comment("菜单表")
public class SecMenuEntity {

    /**
     * 主键ID。
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("主键ID")
    private Long id;

    /**
     * 菜单编码。
     */
    @Column(name = "menu_code", nullable = false, unique = true, length = 64)
    @Comment("菜单编码")
    private String menuCode;

    /**
     * 父级菜单编码。
     */
    @Column(name = "parent_code", length = 64)
    @Comment("父级菜单编码")
    private String parentCode;

    /**
     * 菜单名称。
     */
    @Column(name = "menu_name", nullable = false, length = 128)
    @Comment("菜单名称")
    private String menuName;

    /**
     * 路由路径。
     */
    @Column(name = "path", length = 255)
    @Comment("路由路径")
    private String path;

    /**
     * 前端组件路径。
     */
    @Column(name = "component", length = 255)
    @Comment("前端组件路径")
    private String component;

    /**
     * 关联权限编码。
     */
    @Column(name = "permission_code", length = 128)
    @Comment("关联权限编码")
    private String permissionCode;

    /**
     * 排序值。
     */
    @Column(name = "sort_order")
    @Comment("排序值")
    private Integer sortOrder;

    /**
     * 菜单状态。
     */
    @Column(name = "status", nullable = false, length = 16)
    @Comment("菜单状态")
    private String status;
}
