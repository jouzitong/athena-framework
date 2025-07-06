package org.arthena.framework.common.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.arthena.framework.common.context.SystemContext;
import org.arthena.framework.common.service.IUserContextService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

/**
 * @author zhouzhitong
 * @since 2025/7/6
 **/
@Service
@ConditionalOnMissingBean(IUserContextService.class)
@Slf4j
public class UserContextService implements IUserContextService {

    @Override
    public Long getUserId() {
        LOGGER.warn("UserContextService userId is not configured, please configure it");
        return 0L;
    }

    @Override
    public String getLocale() {
        LOGGER.warn("UserContextService local is not configured, please configure it");
        return SystemContext.getLocale();
    }
}
