package org.athena.framework.security.user.mybatis.service;

import org.apache.commons.lang3.StringUtils;
import org.athena.framework.security.api.auth.CredentialInput;
import org.athena.framework.security.api.auth.CredentialVerifyResult;
import org.athena.framework.security.api.principal.SecurityUser;
import org.athena.framework.security.api.spi.CredentialVerifier;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 基于 JPA 用户数据的凭据校验器。
 * 支持 BCrypt 校验，并兼容明文密码用于历史数据平滑迁移。
 */
public class MybatisCredentialVerifier implements CredentialVerifier {

    private final PasswordEncoder passwordEncoder;

    public MybatisCredentialVerifier(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public CredentialVerifyResult verify(SecurityUser user, CredentialInput input) {
        if (!StringUtils.equalsIgnoreCase(input.credentialType(), "PASSWORD")) {
            return CredentialVerifyResult.failed("UNSUPPORTED_CREDENTIAL_TYPE", "unsupported credential type");
        }
        if (!StringUtils.equalsIgnoreCase(user.status(), "ENABLED")) {
            return CredentialVerifyResult.failed("USER_DISABLED", "user is disabled");
        }
        if (StringUtils.isBlank(user.passwordHash())) {
            return CredentialVerifyResult.failed("CREDENTIAL_NOT_FOUND", "password hash is empty");
        }

        String passwordAlgo = user.passwordAlgo();
        boolean matched = false;

        if (StringUtils.equals(passwordAlgo, "PLAINTEXT")){
            matched = StringUtils.equals(input.password(), user.passwordHash());
        }else if (StringUtils.equals(passwordAlgo, "BCRYPT")){
            matched = passwordEncoder.matches(input.password(), user.passwordHash());
        }
        return matched ? CredentialVerifyResult.ok() : CredentialVerifyResult.failed("BAD_CREDENTIAL", "bad credential");
    }
}
