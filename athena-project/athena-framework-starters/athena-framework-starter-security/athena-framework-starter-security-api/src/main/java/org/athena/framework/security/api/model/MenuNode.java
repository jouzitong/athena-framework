package org.athena.framework.security.api.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单节点模型。
 * 用于承载菜单树结构及其基础展示元数据。
 */
public class MenuNode {

    private String code;

    private String parentCode;

    private String name;

    private String path;

    private String component;

    private String permissionCode;

    private Integer sortOrder;

    private final List<MenuNode> children = new ArrayList<>();

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getPermissionCode() {
        return permissionCode;
    }

    public void setPermissionCode(String permissionCode) {
        this.permissionCode = permissionCode;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public List<MenuNode> getChildren() {
        return children;
    }
}
