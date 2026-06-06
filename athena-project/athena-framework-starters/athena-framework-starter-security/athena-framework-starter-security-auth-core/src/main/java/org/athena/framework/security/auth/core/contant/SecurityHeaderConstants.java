package org.athena.framework.security.auth.core.contant;

/**
 *
 * @author zhouzhitong
 * @since 2026/6/6
 */
public interface SecurityHeaderConstants {

    String HEADER_USER_ID = "X-User-Id";

    String HEADER_USERNAME = "X-Username";

    String HEADER_TENANT_ID = "X-Tenant-Id";

    String HEADER_ROLES = "X-Roles";

    String HEADER_TIMESTAMP = "X-Timestamp";

    String HEADER_PATH = "path";

    String HEADER_SIGN = "X-Gateway-Sign";

    String SIGN_ALGORITHM = "HmacSHA256";

    String SIGN_SEPARATOR = "\n";

}
