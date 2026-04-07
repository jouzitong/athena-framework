package org.athena.framework.security.auth.core.web;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.athena.framework.security.api.auth.AuthenticationRequest;
import org.athena.framework.security.api.auth.AuthenticationResult;
import org.athena.framework.security.auth.core.extractor.CredentialExtractor;
import org.athena.framework.security.auth.core.service.SecurityAuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class SecurityAuthController {

    private final SecurityAuthenticationService securityAuthenticationService;

    private final CredentialExtractor credentialExtractor;

    public SecurityAuthController(SecurityAuthenticationService securityAuthenticationService,
                                  CredentialExtractor credentialExtractor) {
        this.securityAuthenticationService = securityAuthenticationService;
        this.credentialExtractor = credentialExtractor;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginCommand command, HttpServletRequest request) {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(
            command.getUsername(),
            command.getPassword(),
            command.getTenantId(),
            StringUtils.defaultIfBlank(command.getCredentialType(), "PASSWORD"),
            request.getRemoteAddr()
        );
        AuthenticationResult result = securityAuthenticationService.authenticate(authenticationRequest);
        if (!result.success() || result.context() == null || result.context().session() == null) {
            throw new SecurityException("login failed: " + result.code());
        }

        return Map.of(
            "token", result.context().session().tokenId(),
            "user", result.context().subject(),
            "authenticatedAt", result.context().authn().authenticatedAt().toString()
        );
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletRequest request) {
        String token = credentialExtractor.extractToken(request);
        if (StringUtils.isNotBlank(token)) {
            securityAuthenticationService.logout(token);
        }
    }

    @Data
    public static class LoginCommand {

        private String username;

        private String password;

        private String tenantId;

        private String credentialType;
    }
}
