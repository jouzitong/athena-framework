package org.arthena.framework.common.constant;

/**
 * HTTP 请求头约定。
 * 用于统一跟踪、认证和用户信息透传的 header 名称。
 */
public interface RequestHeaderConstant {

    /**
     * 请求链路追踪 ID。
     */
    String TRACE_ID = "X-Trace-Id";

    /**
     * 标准认证头。
     */
    String AUTHORIZATION = "Authorization";

    /**
     * 定义标准认证头的前缀。
     * 该常量用于标识HTTP请求中认证信息头部的名称，确保在处理身份验证时的一致性。
     */
    String AUTHORIZATION_PREFIX = "Authorization";

    /**
     * 业务侧约定的 token 头。
     */
    String TOKEN = "token";

    /**
     * 用户 ID 透传头。
     */
    String USER_ID = "X-User-Id";

    /**
     * 用户名透传头。
     */
    String USER_NAME = "X-User-Name";

    /**
     * 用户显示名透传头。
     */
    String USER_DISPLAY_NAME = "X-User-Display-Name";

    /**
     * 租户 ID 透传头。
     */
    String TENANT_ID = "X-Tenant-Id";

    /**
     * 语言/区域透传头。
     */
    String LOCALE = "lang";
}
