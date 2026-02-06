package org.athena.framework.websocket.backpressure;

public class OutboundQueueFactory {

    private final BackpressureStrategy strategy;
    private final int maxSize;

    public OutboundQueueFactory(BackpressureStrategy strategy, int maxSize) {
        this.strategy = strategy;
        this.maxSize = maxSize;
    }

    public OutboundQueue create() {
        return new OutboundQueue(strategy, maxSize);
    }
}
