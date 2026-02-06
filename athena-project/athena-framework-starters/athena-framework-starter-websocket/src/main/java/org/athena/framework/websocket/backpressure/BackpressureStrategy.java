package org.athena.framework.websocket.backpressure;

public enum BackpressureStrategy {
    DROP_OLD,
    DROP_NEW,
    DISCONNECT_SLOW,
    THROTTLE
}
