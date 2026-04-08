package org.athena.test.security.controller;

import org.athena.framework.security.api.model.UserContext;
import org.athena.framework.security.auth.core.context.SecurityContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/menu")
public class MenuController {

    @GetMapping("/current")
    public List<Map<String, Object>> currentMenus() {
        UserContext userContext = SecurityContextHolder.get();
        if (userContext == null || userContext.subject() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
        }

        Set<String> permissions = userContext.authorization() == null || userContext.authorization().permissions() == null
            ? Set.of() : userContext.authorization().permissions();

        List<Map<String, Object>> menus = new ArrayList<>();
        if (permissions.contains("menu:view")) {
            Map<String, Object> menu = new LinkedHashMap<>();
            menu.put("code", "MENU_DASHBOARD");
            menu.put("name", "Dashboard");
            menu.put("permissionCode", "menu:view");
            menus.add(menu);
        }
        if (permissions.contains("user:read")) {
            Map<String, Object> menu = new LinkedHashMap<>();
            menu.put("code", "MENU_USER_LIST");
            menu.put("name", "Users");
            menu.put("permissionCode", "user:read");
            menus.add(menu);
        }
        if (permissions.contains("user:create")) {
            Map<String, Object> menu = new LinkedHashMap<>();
            menu.put("code", "MENU_USER_CREATE");
            menu.put("name", "Create User");
            menu.put("permissionCode", "user:create");
            menus.add(menu);
        }
        return menus;
    }
}
