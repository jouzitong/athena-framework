package org.athena.framework.data.jdbc.chain;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.athena.framework.data.jdbc.context.CrudContext;
import org.athena.framework.data.jdbc.properties.DefaultJdbcProperties;
import org.athena.framework.data.jdbc.serivce.IJdbcCrudInterceptor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author zhouzhitong
 * @since 2026/1/15
 */
@Slf4j
@Service
public class CrudInterceptorChain implements CommandLineRunner {

    @Resource
    private final List<IJdbcCrudInterceptor> interceptors = new ArrayList<>();


    private final Map<Class<?>, List<IJdbcCrudInterceptor>> entityInterceptorsMap = new ConcurrentHashMap<>();

    @Override
    public void run(String... args) throws Exception {
        for (IJdbcCrudInterceptor interceptor : interceptors) {
            Class<?> entityType = interceptor.entityType();
            if (!entityInterceptorsMap.containsKey(entityType)) {
                entityInterceptorsMap.put(entityType, new ArrayList<>());
            }
            entityInterceptorsMap.get(entityType).add(interceptor);
            entityInterceptorsMap.get(entityType).sort(Comparator.comparingInt(Ordered::getOrder));
        }
    }

    public boolean register(IJdbcCrudInterceptor interceptor) {
        Class<?> entityType = interceptor.entityType();
        if (!entityInterceptorsMap.containsKey(entityType)) {
            entityInterceptorsMap.put(entityType, new ArrayList<>());
        }
        entityInterceptorsMap.get(entityType).add(interceptor);
        entityInterceptorsMap.get(entityType).sort(Comparator.comparingInt(Ordered::getOrder));
        return true;
    }

    public boolean unRegister(IJdbcCrudInterceptor interceptor) {
        Class<?> entityType = interceptor.entityType();
        if (entityInterceptorsMap.containsKey(entityType)) {
            boolean removed = entityInterceptorsMap.get(entityType).remove(interceptor);
            if (removed) {
                entityInterceptorsMap.get(entityType).sort(Comparator.comparingInt(Ordered::getOrder));
            }
            return removed;
        }
        return false;
    }

    public boolean beforeCheck(CrudContext context) {
        List<IJdbcCrudInterceptor> interceptors = entityInterceptorsMap.get(context.getEntityType());
        for (IJdbcCrudInterceptor interceptor : interceptors) {
            if (!interceptor.beforeCheck(context)) {
                return false;
            }
        }
        return true;
    }

    public boolean before(CrudContext context) {
        List<IJdbcCrudInterceptor> interceptors = entityInterceptorsMap.get(context.getEntityType());
        for (IJdbcCrudInterceptor interceptor : interceptors) {
            if (!interceptor.before(context)) {
                return false;
            }
        }
        return true;
    }

    public void after(CrudContext context) {
        List<IJdbcCrudInterceptor> interceptors = entityInterceptorsMap.get(context.getEntityType());
        for (IJdbcCrudInterceptor interceptor : interceptors) {
            interceptor.after(context);
        }
    }

    public void onError(CrudContext context) {
        List<IJdbcCrudInterceptor> interceptors = entityInterceptorsMap.get(context.getEntityType());
        for (IJdbcCrudInterceptor interceptor : interceptors) {
            interceptor.onError(context);
        }
    }

}
