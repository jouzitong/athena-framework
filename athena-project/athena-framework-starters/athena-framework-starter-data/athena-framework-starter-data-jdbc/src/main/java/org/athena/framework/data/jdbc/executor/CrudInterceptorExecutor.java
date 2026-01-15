package org.athena.framework.data.jdbc.executor;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.athena.framework.data.jdbc.chain.CrudInterceptorChain;
import org.athena.framework.data.jdbc.context.CrudContext;
import org.athena.framework.data.jdbc.properties.DefaultJdbcProperties;
import org.springframework.stereotype.Service;

/**
 *
 * @author zhouzhitong
 * @since 2026/1/15
 */
@Service
@Slf4j
public class CrudInterceptorExecutor {

    @Resource
    private DefaultJdbcProperties jdbcProperties;

    @Resource
    private CrudInterceptorChain interceptorChain;

    public boolean beforeCheck(CrudContext context) {
        if (isEnabled()) {
            return true;
        }
        return interceptorChain.beforeCheck(context);
    }

    public boolean before(CrudContext context) {
        if (isEnabled()) {
            return true;
        }
        return interceptorChain.before(context);
    }

    public void after(CrudContext context) {
        if (isEnabled()) {
            return;
        }
        interceptorChain.after(context);
    }

    public void onError(CrudContext context) {
        if (isEnabled()) {
            return;
        }
        interceptorChain.onError(context);
    }

    protected boolean isEnabled() {
        return jdbcProperties.isEnableEvent();
    }


}
