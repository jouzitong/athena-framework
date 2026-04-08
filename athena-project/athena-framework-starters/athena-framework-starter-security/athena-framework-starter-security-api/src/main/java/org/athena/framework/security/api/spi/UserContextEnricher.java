package org.athena.framework.security.api.spi;

import org.athena.framework.security.api.model.MutableUserContext;

/**
 * 用户上下文增强器扩展点。
 * 用于在认证后追加权限、角色、组织等扩展信息。
 */
public interface UserContextEnricher {

    int order();

    void enrich(MutableUserContext context);
}
