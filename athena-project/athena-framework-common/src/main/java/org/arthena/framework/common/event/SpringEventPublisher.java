package org.arthena.framework.common.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

/**
 * 事件发布器
 *
 * @author zhouzhitong
 */
@Slf4j
public class SpringEventPublisher implements EventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public SpringEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void publish(IEvent event) {
        LOGGER.debug("publish event: {}", event);
        applicationEventPublisher.publishEvent(event);
        LOGGER.trace("publish finish.");
    }
}
