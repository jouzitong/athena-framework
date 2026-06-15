package org.arthena.framework.common.event;

/**
 * @author zhouzhitong
 */
public interface EventPublisher {
    /**
     * 发布一个事件
     *
     * @param event 事件对象
     */
    void publish(IEvent event);
}
