package org.arthena.framework.common.thread.schedule;

import org.arthena.framework.common.thread.AsyncTaskExcutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.util.concurrent.TimeUnit;

/**
 * 任务管理器
 *
 * @deprecated 请改用 {@link org.arthena.framework.common.thread.AsyncTaskExcutor}，
 * 定时循环任务使用
 * {@link org.arthena.framework.common.thread.AsyncTaskExcutor#scheduleWithFixedDelay(Runnable, long, long, TimeUnit)}
 * 或
 * {@link org.arthena.framework.common.thread.AsyncTaskExcutor#scheduleAtFixedRate(Runnable, long, long, TimeUnit)}。
 *
 * @author zhouzhitong
 * @since 2021/8/16
 */
@Component
@Slf4j
@Deprecated(since = "1.4.2", forRemoval = true)
public class ScheduleMonitor implements Closeable {

    private final AsyncTaskExcutor asyncTaskExcutor;

    public ScheduleMonitor(AsyncTaskExcutor asyncTaskExcutor) {
        this.asyncTaskExcutor = asyncTaskExcutor;
    }

    /**
     * 修改任务可用核心线程数
     *
     * @param poolSize 核心线程数
     * @deprecated 请改用 {@link org.arthena.framework.common.thread.AsyncTaskExcutor#setSchedulerPoolSize(int)}
     */
    @Deprecated(since = "1.4.2", forRemoval = true)
    public void setPoolSize(Integer poolSize) {
        asyncTaskExcutor.setSchedulerPoolSize(poolSize);
    }

    /**
     * @param runnable 需要执行的任务
     * @param period   任务执行间隔时间
     * @param timeUnit 间隔时间单位
     * @deprecated 请改用
     * {@link org.arthena.framework.common.thread.AsyncTaskExcutor#scheduleWithFixedDelay(Runnable, long, long, TimeUnit)}
     */
    @Deprecated(since = "1.4.2", forRemoval = true)
    public String schedule(Runnable runnable, Long period, TimeUnit timeUnit) {
        return asyncTaskExcutor.scheduleWithFixedDelay(runnable, 0L, period, timeUnit);
    }

    /**
     * 取消任务
     *
     * @param id 任务ID
     * @return 是否取消成功
     * @deprecated 请改用 {@link org.arthena.framework.common.thread.AsyncTaskExcutor#cancel(String)}
     */
    @Deprecated(since = "1.4.2", forRemoval = true)
    public boolean cancel(String id) {
        return asyncTaskExcutor.cancel(id);
    }

    /**
     * 关闭定时任务
     *
     * @deprecated 请改用 {@link org.arthena.framework.common.thread.AsyncTaskExcutor#close()}
     */
    @Deprecated(since = "1.4.2", forRemoval = true)
    @Override
    public void close() {
        asyncTaskExcutor.close();
    }
}
