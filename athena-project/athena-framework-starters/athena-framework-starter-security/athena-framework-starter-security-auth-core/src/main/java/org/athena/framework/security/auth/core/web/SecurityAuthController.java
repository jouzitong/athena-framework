package org.athena.framework.security.auth.core.web;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.arthena.framework.common.constant.CodeConstant;
import org.arthena.framework.common.exception.base.BaseHttpRuntimeException;
import org.athena.framework.security.api.auth.AuthenticationRequest;
import org.athena.framework.security.api.auth.AuthenticationResult;
import org.athena.framework.security.api.model.UserContext;
import org.athena.framework.security.auth.core.context.SecurityContextHolder;
import org.athena.framework.security.auth.core.extractor.CredentialExtractor;
import org.athena.framework.security.auth.core.service.SecurityAuthenticationFacade;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
/**
 * 默认认证接口控制器。
 * 对外暴露登录和登出接口，并与认证应用服务联动。
 */
public class SecurityAuthController {

    private final SecurityAuthenticationFacade securityAuthenticationService;

    private final CredentialExtractor credentialExtractor;

    public SecurityAuthController(SecurityAuthenticationFacade securityAuthenticationService,
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
            throw new BaseHttpRuntimeException(HttpStatus.UNAUTHORIZED.value(), CodeConstant.LOGIN_FAILED);
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

    @PostMapping("/refresh")
    public Map<String, Object> refresh(HttpServletRequest request) {
        String token = credentialExtractor.extractToken(request);
        AuthenticationResult result = securityAuthenticationService.refresh(token);
        if (!result.success() || result.context() == null || result.context().session() == null) {
            int code;
            if ("TOKEN_EMPTY".equals(result.code())) {
                code = CodeConstant.UNAUTHORIZED;
            } else if ("TOKEN_EXPIRED".equals(result.code())) {
                code = CodeConstant.TOKEN_EXPIRED;
            } else if ("TOKEN_INVALID".equals(result.code())) {
                code = CodeConstant.TOKEN_INVALID;
            } else {
                code = CodeConstant.UNAUTHORIZED;
            }
            throw new BaseHttpRuntimeException(HttpStatus.UNAUTHORIZED.value(), code);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("token", result.context().session().tokenId());
        response.put("user", result.context().subject());
        response.put("authenticatedAt", result.context().authn() == null ? null : result.context().authn().authenticatedAt().toString());
        return response;
    }

    @GetMapping("/me")
    public Map<String, Object> me() {
        UserContext userContext = SecurityContextHolder.get();
        if (userContext == null || userContext.subject() == null) {
            throw new BaseHttpRuntimeException(HttpStatus.UNAUTHORIZED.value(), CodeConstant.UNAUTHORIZED);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("user", userContext.subject());
        response.put("displayName", userContext.attributes() == null ? null : userContext.attributes().get("displayName"));
        response.put("authn", userContext.authn());
        response.put("session", userContext.session());
        return response;
    }

    @Data
    /**
     * 登录命令对象。
     * 承载登录接口请求体字段。
     */
    public static class LoginCommand {

        private String username;

        private String password;

        private String tenantId;

        private String credentialType;
    }
}
