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
 * CRUD 拦截器责任链管理器。
 * <p>
 * 负责维护所有 {@link IJdbcCrudInterceptor} 的注册关系，按实体类型分组，
 * 并在每个分组内按照 {@link Ordered#getOrder()} 升序执行。
 * 同时提供标准生命周期回调入口：{@code beforeCheck}、{@code before}、{@code after}、{@code onError}。
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

    /**
     * 启动后初始化拦截器链。
     * <p>
     * 按实体类型分组，并在每个分组内按 {@link Ordered#getOrder()} 从小到大排序。
     *
     * @param args 启动参数
     * @throws Exception 初始化过程中抛出的异常
     */
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

    /**
     * 注册一个 CRUD 拦截器。
     * <p>
     * 注册后会立即按执行顺序重新排序。
     *
     * @param interceptor 待注册的拦截器
     * @return 始终返回 {@code true}
     */
    public boolean register(IJdbcCrudInterceptor interceptor) {
        Class<?> entityType = interceptor.entityType();
        if (!entityInterceptorsMap.containsKey(entityType)) {
            entityInterceptorsMap.put(entityType, new ArrayList<>());
        }
        entityInterceptorsMap.get(entityType).add(interceptor);
        entityInterceptorsMap.get(entityType).sort(Comparator.comparingInt(Ordered::getOrder));
        return true;
    }

    /**
     * 取消注册一个 CRUD 拦截器。
     *
     * @param interceptor 待取消注册的拦截器
     * @return 取消成功返回 {@code true}；未找到对应实体类型或拦截器时返回 {@code false}
     */
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

    /**
     * 执行前置校验阶段。
     * <p>
     * 任一拦截器返回 {@code false} 时立即短路并返回 {@code false}。
     *
     * @param context CRUD 上下文
     * @return 全部通过返回 {@code true}，否则返回 {@code false}
     */
    public boolean beforeCheck(CrudContext context) {
        List<IJdbcCrudInterceptor> interceptors = entityInterceptorsMap.get(context.getEntityType());
        for (IJdbcCrudInterceptor interceptor : interceptors) {
            if (!interceptor.beforeCheck(context)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 执行前置处理阶段。
     * <p>
     * 任一拦截器返回 {@code false} 时立即短路并返回 {@code false}。
     *
     * @param context CRUD 上下文
     * @return 全部通过返回 {@code true}，否则返回 {@code false}
     */
    public boolean before(CrudContext context) {
        List<IJdbcCrudInterceptor> interceptors = entityInterceptorsMap.get(context.getEntityType());
        for (IJdbcCrudInterceptor interceptor : interceptors) {
            if (!interceptor.before(context)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 执行后置处理阶段。
     *
     * @param context CRUD 上下文
     */
    public void after(CrudContext context) {
        List<IJdbcCrudInterceptor> interceptors = entityInterceptorsMap.get(context.getEntityType());
        for (IJdbcCrudInterceptor interceptor : interceptors) {
            interceptor.after(context);
        }
    }

    /**
     * 执行异常回调阶段。
     *
     * @param context CRUD 上下文
     */
    public void onError(CrudContext context) {
        List<IJdbcCrudInterceptor> interceptors = entityInterceptorsMap.get(context.getEntityType());
        for (IJdbcCrudInterceptor interceptor : interceptors) {
            interceptor.onError(context);
        }
    }

}
