package org.athena.test.security.controller;

import org.athena.framework.security.api.annotation.RequirePermission;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private static final List<Map<String, String>> USERS = new ArrayList<>(List.of(
        Map.of("username", "admin"),
        Map.of("username", "operator"),
        Map.of("username", "guest")
    ));

    @GetMapping("/users")
    @RequirePermission("user:read")
    public List<Map<String, String>> users() {
        return USERS;
    }

    @PostMapping("/users")
    @RequirePermission("user:create")
    public Map<String, Object> createUser(@RequestBody CreateUserCommand command) {
        USERS.add(Map.of("username", command.username()));
        return Map.of("created", true, "username", command.username(), "size", USERS.size());
    }

    public record CreateUserCommand(String username) {
    }
}
