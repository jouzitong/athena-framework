package org.athena.framework.security.api.spi;

import org.athena.framework.security.api.model.MenuNode;
import org.athena.framework.security.api.model.UserContext;

import java.util.List;

/**
 * 菜单提供者扩展点。
 * 按当前用户上下文加载可见菜单树。
 */
public interface MenuProvider {

    List<MenuNode> loadMenus(UserContext context);
}
