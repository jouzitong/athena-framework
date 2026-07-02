package org.arthena.framework.common.thread.context;

/**
 * 异步任务上下文传播器。
 * 用于在任务提交线程和任务执行线程之间传递 ThreadLocal 等线程上下文。
 *
 * @author zhouzhitong
 * @since 2026/7/2
 */
public interface AsyncTaskContextPropagator {

    /**
     * 从提交任务线程捕获上下文快照。
     *
     * @return 上下文快照
     */
    Object capture();

    /**
     * 将快照安装到当前执行线程，并返回执行线程原有上下文备份。
     *
     * @param snapshot 提交任务时捕获的快照
     * @return 当前执行线程原有上下文备份
     */
    Object install(Object snapshot);

    /**
     * 恢复当前执行线程原有上下文。
     *
     * @param backup install 时返回的上下文备份
     */
    void restore(Object backup);

    /**
     * 执行顺序，值越小越早执行。
     *
     * @return 顺序值
     */
    default int getOrder() {
        return 0;
    }
}
