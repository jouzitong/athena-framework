package org.athena.framework.websocket.backpressure;

import java.util.ArrayDeque;
import java.util.Deque;

public class OutboundQueue {

    private final BackpressureStrategy strategy;
    private final int maxSize;
    private final Deque<String> queue = new ArrayDeque<>();

    public OutboundQueue(BackpressureStrategy strategy, int maxSize) {
        this.strategy = strategy;
        this.maxSize = maxSize;
    }

    public boolean offer(String payload) {
        if (payload == null) {
            return false;
        }
        if (queue.size() < maxSize) {
            queue.addLast(payload);
            return true;
        }
        // 队列满时按策略处理
        if (strategy == BackpressureStrategy.DROP_OLD) {
            queue.pollFirst();
            queue.addLast(payload);
            return true;
        }
        if (strategy == BackpressureStrategy.DROP_NEW) {
            return false;
        }
        return false;
    }

    public String poll() {
        return queue.pollFirst();
    }

    public int size() {
        return queue.size();
    }
}
