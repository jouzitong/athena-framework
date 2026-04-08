package org.athena.framework.security.api.spi;

import org.athena.framework.security.api.auth.CredentialInput;
import org.athena.framework.security.api.auth.CredentialVerifyResult;
import org.athena.framework.security.api.principal.SecurityUser;

/**
 * 凭据校验器扩展点。
 * 负责校验用户输入凭据与存储凭据是否匹配。
 */
public interface CredentialVerifier {

    CredentialVerifyResult verify(SecurityUser user, CredentialInput input);
}
