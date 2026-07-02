package org.arthena.framework.common.thread.context;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * MDC 上下文传播器。
 *
 * @author zhouzhitong
 * @since 2026/7/2
 */
@Component
public class MdcContextPropagator implements AsyncTaskContextPropagator {

    @Override
    public Object capture() {
        return copy(MDC.getCopyOfContextMap());
    }

    @Override
    public Object install(Object snapshot) {
        Map<String, String> previous = copy(MDC.getCopyOfContextMap());
        Map<String, String> contextMap = cast(snapshot);
        if (contextMap == null || contextMap.isEmpty()) {
            MDC.clear();
        } else {
            MDC.setContextMap(contextMap);
        }
        return previous;
    }

    @Override
    public void restore(Object backup) {
        Map<String, String> contextMap = cast(backup);
        if (contextMap == null || contextMap.isEmpty()) {
            MDC.clear();
        } else {
            MDC.setContextMap(contextMap);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> cast(Object value) {
        return (Map<String, String>) value;
    }

    private Map<String, String> copy(Map<String, String> contextMap) {
        if (contextMap == null || contextMap.isEmpty()) {
            return null;
        }
        return new HashMap<>(contextMap);
    }
}
