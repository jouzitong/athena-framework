package org.athena.test.security.controller;

import org.athena.framework.security.api.model.UserContext;
import org.athena.framework.security.auth.core.context.SecurityContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    @GetMapping("/me")
    public Map<String, Object> me() {
        UserContext userContext = SecurityContextHolder.get();
        if (userContext == null || userContext.subject() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
        }
        return Map.of(
            "subject", userContext.subject(),
            "authn", userContext.authn(),
            "authorization", userContext.authorization(),
            "session", userContext.session(),
            "attributes", userContext.attributes()
        );
    }

    @GetMapping("/ping")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, String> ping() {
        return Map.of("status", "ok");
    }
}
