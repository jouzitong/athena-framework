package org.athena.framework.datasource.context;

import java.util.ArrayDeque;
import java.util.Deque;

public final class RouteContext {

    private static final ThreadLocal<Deque<String>> CONTEXT = ThreadLocal.withInitial(ArrayDeque::new);

    private RouteContext() {
    }

    public static void push(String key) {
        if (key == null || key.isBlank()) {
            return;
        }
        CONTEXT.get().push(key);
    }

    public static String peek() {
        Deque<String> stack = CONTEXT.get();
        return stack.isEmpty() ? null : stack.peek();
    }

    public static void poll() {
        Deque<String> stack = CONTEXT.get();
        if (!stack.isEmpty()) {
            stack.poll();
        }
        if (stack.isEmpty()) {
            CONTEXT.remove();
        }
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
