package org.athena.test.security.controller;

import org.athena.framework.security.api.model.MenuNode;
import org.athena.framework.security.api.model.UserContext;
import org.athena.framework.security.api.spi.MenuProvider;
import org.athena.framework.security.auth.core.context.SecurityContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/menu")
public class MenuController {

    private final MenuProvider menuProvider;

    public MenuController(MenuProvider menuProvider) {
        this.menuProvider = menuProvider;
    }

    @GetMapping("/current")
    public List<MenuNode> currentMenus() {
        UserContext userContext = SecurityContextHolder.get();
        if (userContext == null || userContext.subject() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
        }
        return menuProvider.loadMenus(userContext);
    }
}
