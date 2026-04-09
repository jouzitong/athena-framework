package org.athena.framework.security.user.jpa.service.menu;

import org.apache.commons.lang3.StringUtils;
import org.athena.framework.security.api.model.MenuNode;
import org.athena.framework.security.api.model.UserContext;
import org.athena.framework.security.api.spi.MenuProvider;
import org.athena.framework.security.user.jpa.entity.SecMenuEntity;
import org.athena.framework.security.user.jpa.repository.SecMenuJpaRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JpaMenuProvider implements MenuProvider {

    private final SecMenuJpaRepository menuRepository;

    public JpaMenuProvider(SecMenuJpaRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    @Override
    public List<MenuNode> loadMenus(UserContext context) {
        Set<String> permissions = context == null || context.authorization() == null || context.authorization().permissions() == null
            ? Set.of()
            : new LinkedHashSet<>(context.authorization().permissions());

        List<SecMenuEntity> visibleMenus = menuRepository.findAllByOrderBySortOrderAscIdAsc()
            .stream()
            .filter(menu -> StringUtils.equalsIgnoreCase(menu.getStatus(), "ENABLED"))
            .filter(menu -> StringUtils.isBlank(menu.getPermissionCode()) || permissions.contains(menu.getPermissionCode()))
            .toList();

        Map<String, MenuNode> index = new LinkedHashMap<>();
        for (SecMenuEntity entity : visibleMenus) {
            MenuNode node = new MenuNode();
            node.setCode(entity.getMenuCode());
            node.setParentCode(entity.getParentCode());
            node.setName(entity.getMenuName());
            node.setPath(entity.getPath());
            node.setComponent(entity.getComponent());
            node.setPermissionCode(entity.getPermissionCode());
            node.setSortOrder(entity.getSortOrder());
            index.put(node.getCode(), node);
        }

        List<MenuNode> roots = new ArrayList<>();
        for (MenuNode node : index.values()) {
            if (StringUtils.isBlank(node.getParentCode())) {
                roots.add(node);
                continue;
            }
            MenuNode parent = index.get(node.getParentCode());
            if (parent == null) {
                roots.add(node);
                continue;
            }
            parent.getChildren().add(node);
        }
        return roots;
    }
}
