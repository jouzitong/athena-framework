package org.athena.framework.security.auth.core.gateway;

/**
 * Gateway 透传请求头约定。
 */
public interface GatewayRequestHeaderConstants {

    String USER_ID = "X-User-Id";

    String USERNAME = "X-Username";

    String TENANT_ID = "X-Tenant-Id";

    String ROLES = "X-Roles";

    String TIMESTAMP = "X-Timestamp";

    String PATH = "path";

    String SIGN = "X-Gateway-Sign";

    String AUTH_TYPE = "GATEWAY_HEADER";
}
