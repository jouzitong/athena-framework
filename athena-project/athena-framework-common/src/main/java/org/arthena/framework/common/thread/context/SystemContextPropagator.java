package org.arthena.framework.common.thread.context;

import org.arthena.framework.common.context.SystemContext;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * SystemContext 上下文传播器。
 *
 * @author zhouzhitong
 * @since 2026/7/2
 */
@Component
public class SystemContextPropagator implements AsyncTaskContextPropagator {

    @Override
    public Object capture() {
        return SystemContext.snapshot();
    }

    @Override
    public Object install(Object snapshot) {
        Map<String, Object> previous = SystemContext.snapshot();
        SystemContext.restore(cast(snapshot));
        return previous;
    }

    @Override
    public void restore(Object backup) {
        SystemContext.restore(cast(backup));
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> cast(Object value) {
        return (Map<String, Object>) value;
    }
}
