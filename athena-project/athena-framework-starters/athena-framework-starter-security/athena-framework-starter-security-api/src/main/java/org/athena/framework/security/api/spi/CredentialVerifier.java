package org.athena.framework.security.api.spi;

import org.athena.framework.security.api.auth.CredentialInput;
import org.athena.framework.security.api.auth.CredentialVerifyResult;
import org.athena.framework.security.api.principal.SecurityUser;

public interface CredentialVerifier {

    CredentialVerifyResult verify(SecurityUser user, CredentialInput input);
}
