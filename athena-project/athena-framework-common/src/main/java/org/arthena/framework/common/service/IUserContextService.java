package org.arthena.framework.common.service;

/**
 * @author zhouzhitong
 * @since 2025/7/6
 **/
public interface IUserContextService {

    /**
     *
     * @return
     */
    Long getUserId();

    /**
     * 当前租户ID
     *
     * @return tenantId
     */
    Long getTenantId();

    String getLocale();

}
