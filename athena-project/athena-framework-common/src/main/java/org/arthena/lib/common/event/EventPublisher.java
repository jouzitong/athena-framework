package org.arthena.lib.common.event;

/**
 * @author zhouzhitong
 */
public interface EventPublisher {

    /**
     * 发布一个事件
     *
     * @param event
     */
    void publish(Object event);
}
