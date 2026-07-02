package org.arthena.framework.common.thread;

import java.time.Instant;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 系统异步任务管理器，统一管理普通任务、延迟任务、定时任务和循环任务。
 *
 * @author zhouzhitong
 * @since 2026/7/2
 */
public interface AsyncTaskManager {

    /**
     * 提交一个无返回值的异步任务，任务会立即进入异步线程池等待执行。
     *
     * @param runnable 待执行的任务
     * @return 任务唯一标识
     */
    String submit(Runnable runnable);

    /**
     * 提交一个带返回值的异步任务，任务会立即进入异步线程池等待执行。
     * <p>
     * 当前接口仅返回任务唯一标识，具体执行结果可由实现类自行扩展存储或查询能力。
     *
     * @param callable 待执行的任务
     * @param <T>      任务返回值类型
     * @return 任务唯一标识
     */
    <T> String submit(Callable<T> callable);

    /**
     * 提交一个延迟执行任务，任务会在指定延迟时间后执行一次。
     *
     * @param runnable 待执行的任务
     * @param delay    延迟时间
     * @param timeUnit 延迟时间单位
     * @return 任务唯一标识
     */
    String schedule(Runnable runnable, long delay, TimeUnit timeUnit);

    /**
     * 提交一个指定时间点执行的任务，任务会在指定时间到达后执行一次。
     *
     * @param runnable  待执行的任务
     * @param executeAt 指定执行时间点
     * @return 任务唯一标识
     */
    String scheduleAt(Runnable runnable, Instant executeAt);

    /**
     * 提交一个固定频率执行的定时任务。
     * <p>
     * 下一次执行时间按固定周期计算，不受上一次任务执行耗时影响；如果任务执行耗时超过周期，可能会出现连续补偿执行。
     *
     * @param runnable     待执行的任务
     * @param initialDelay 首次执行前的延迟时间
     * @param period       两次任务开始执行之间的固定周期
     * @param timeUnit     时间单位
     * @return 任务唯一标识
     */
    String scheduleAtFixedRate(Runnable runnable, long initialDelay, long period, TimeUnit timeUnit);

    /**
     * 提交一个固定延迟执行的循环任务。
     * <p>
     * 下一次执行会在上一次任务执行完成后，再等待指定延迟时间。
     *
     * @param runnable     待执行的任务
     * @param initialDelay 首次执行前的延迟时间
     * @param delay        上一次执行完成到下一次执行开始之间的延迟时间
     * @param timeUnit     时间单位
     * @return 任务唯一标识
     */
    String scheduleWithFixedDelay(Runnable runnable, long initialDelay, long delay, TimeUnit timeUnit);

    /**
     * 取消指定任务。
     * <p>
     * 对于尚未执行的任务，取消后将不会继续执行；对于正在执行的任务，是否中断由具体实现决定。
     *
     * @param taskId 任务唯一标识
     * @return true 表示取消成功，false 表示任务不存在或取消失败
     */
    boolean cancel(String taskId);

    /**
     * 判断指定任务是否存在于当前任务管理器中。
     *
     * @param taskId 任务唯一标识
     * @return true 表示任务存在，false 表示任务不存在
     */
    boolean contains(String taskId);

    /**
     * 获取当前任务管理器中维护的任务数量。
     *
     * @return 当前任务数量
     */
    int getTaskCount();

    /**
     * 获取当前任务管理器中维护的全部任务唯一标识。
     *
     * @return 任务唯一标识集合
     */
    Set<String> getTaskIds();

    /**
     * 设置普通异步任务线程池大小。
     * <p>
     * 是否支持运行时动态调整，由具体实现决定。
     *
     * @param poolSize 线程池大小
     */
    void setAsyncPoolSize(int poolSize);

    /**
     * 设置调度任务线程池大小。
     * <p>
     * 是否支持运行时动态调整，由具体实现决定。
     *
     * @param poolSize 线程池大小
     */
    void setSchedulerPoolSize(int poolSize);

    /**
     * 关闭任务管理器并释放相关线程池资源。
     * <p>
     * 关闭后不应再提交新的异步任务、延迟任务或定时任务。
     */
    void close();
}
