package org.arthena.lib.common.thread.schedule;

import org.arthena.lib.common.thread.factory.ScheduleThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 任务管理器
 *
 * @author zhouzhitong
 * @since 2021/8/16
 */
@Component
@Slf4j
public class ScheduleMonitor implements Closeable {

    /**
     * 任务集合
     */
    private final ConcurrentHashMap<Long, ScheduledFuture<?>> taskMap;

    /**
     * id生成器
     */
    private final AtomicLong idGenerator;

    /**
     * 任务调度器
     */
    private ThreadPoolTaskScheduler scheduler;

    /**
     * 构造函数: 初始化任务集合和id生成器
     */
    public ScheduleMonitor() {
        taskMap = new ConcurrentHashMap<>(16);
        idGenerator = new AtomicLong(1L);
        initScheduler();
    }

    /**
     * 初始化任务调度器
     */
    private void initScheduler() {
        scheduler = new ThreadPoolTaskScheduler();
        // 设置定时任务线程数
        scheduler.setPoolSize(Runtime.getRuntime().availableProcessors() >> 1);

        scheduler.setThreadFactory(new ScheduleThreadFactory("定时任务线程池-定时任务"));
        //进行初始化
        scheduler.initialize();
//        scheduler.setRejectedExecutionHandler(null);
    }

    /**
     * 修改任务可用核心线程数
     *
     * @param poolSize 核心线程数
     */
    public void setPoolSize(Integer poolSize) {
        scheduler.setPoolSize(poolSize);
    }

    /**
     * @param runnable 需要执行的任务
     * @param period   任务执行间隔时间
     * @param timeUnit 间隔时间单位
     */
    public Long schedule(Runnable runnable, Long period, TimeUnit timeUnit) {
        Long id = idGenerator.getAndIncrement();
        Duration duration = Duration.of(period, timeUnit.toChronoUnit());
        ScheduledFuture<?> schedule = scheduler.schedule(runnable, new PeriodicTrigger(duration));
        if (schedule != null) {
            taskMap.put(id, schedule);
        }
        return id;
    }

    /**
     * 取消任务
     *
     * @param id 任务ID
     * @return 是否取消成功
     */
    public boolean cancel(Long id) {
        if (!taskMap.containsKey(id)) {
            return true;
//            throw new RuntimeException(String.format("删除任务失败，指定的任务{id = %s} 不存在", id));
        }
        ScheduledFuture<?> remove = taskMap.remove(id);
        // 多线程操作时，remove 可能为 null
        if (remove == null) {
            return true;
        }
        return remove.cancel(false);
    }

    /**
     * 关闭定时任务
     *
     * @throws IOException 关闭异常
     */
    @Override
    public void close() throws IOException {
        scheduler.shutdown();
        taskMap.clear();
    }
}
