package org.athena.framework.security.api.spi;

import org.athena.framework.security.api.auth.AuthenticationRequest;
import org.athena.framework.security.api.auth.AuthenticationResult;

public interface Authenticator {

    AuthenticationResult authenticate(AuthenticationRequest request);
}
