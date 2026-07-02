package org.arthena.framework.common.thread.factory;

/**
 * 定时任务线程工厂
 *
 * @author zhouzhitong
 * @since 2021/8/17
 */
public class ScheduleThreadFactory extends NamedThreadFactory {

    public ScheduleThreadFactory(String poolName) {
        super(poolName);
    }
}
