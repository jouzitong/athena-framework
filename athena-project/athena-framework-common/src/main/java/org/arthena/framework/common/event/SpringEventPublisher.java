package org.arthena.framework.common.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

/**
 * 事件发布器
 *
 * @author zhouzhitong
 */
@Component("springEventPublisher")
@Slf4j
public class SpringEventPublisher implements EventPublisher, ApplicationEventPublisherAware {

    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(Object event) {
        LOGGER.debug("publish event: {}", event);
        applicationEventPublisher.publishEvent(event);
        LOGGER.trace("publish finish.");
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
