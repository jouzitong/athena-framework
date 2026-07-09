package org.arthena.framework.common.service.impl;

import org.arthena.framework.common.service.ErrorCodeService;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Service;

/**
 *
 * @author zhouzhitong
 * @since 2026/7/9
 */
@Service
public class EmptyErrorCodeServiceImpl implements ErrorCodeService {

    @Override
    public int order() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public String getMsg(Integer code, String locale) {
        return null;
    }
}
