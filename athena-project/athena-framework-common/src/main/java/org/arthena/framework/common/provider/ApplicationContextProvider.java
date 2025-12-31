package org.arthena.framework.common.provider;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * spring context provider
 *
 * @author zhouzhitong
 * @version 1.0
 * @since 2022/6/19
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApplicationContextProvider implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextProvider.applicationContext = applicationContext;
    }

    public static ApplicationContext getContext() {
        return ApplicationContextProvider.applicationContext;
    }

    public static <T> List<T> getBeansOfType(Class<T> requiredType) {
        Map<String, T> beansOfType = applicationContext.getBeansOfType(requiredType);
        return List.copyOf(beansOfType.values());
    }

    public static <T> T getBean(Class<T> requiredType) {
        return applicationContext.getBean(requiredType);
    }


}
