package org.arthena.framework.common.thread;

import org.arthena.framework.common.thread.context.AsyncTaskContextPropagator;
import org.arthena.framework.common.thread.factory.NamedThreadFactory;
import org.arthena.framework.common.utils.IdUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 默认异步任务执行器实现。
 *
 * @author zhouzhitong
 * @since 2026/7/2
 */
@Component
@Slf4j
public class DefaultAsyncTaskExcutor implements AsyncTaskExcutor {

    private final ConcurrentMap<String, ManagedTask> taskMap = new ConcurrentHashMap<>(32);

    private final AtomicBoolean closed = new AtomicBoolean(false);

    private final List<AsyncTaskContextPropagator> propagators;

    private final ThreadPoolExecutor asyncExecutor;

    private final ScheduledThreadPoolExecutor scheduler;

    public DefaultAsyncTaskExcutor() {
        this(Collections.emptyList());
    }

    @Autowired
    public DefaultAsyncTaskExcutor(List<AsyncTaskContextPropagator> propagators) {
        List<AsyncTaskContextPropagator> orderedPropagators = new ArrayList<>(propagators);
        orderedPropagators.sort(Comparator.comparingInt(AsyncTaskContextPropagator::getOrder));
        this.propagators = Collections.unmodifiableList(orderedPropagators);

        int processors = Math.max(2, Runtime.getRuntime().availableProcessors());
        this.asyncExecutor = new ThreadPoolExecutor(
            processors,
            processors << 1,
            60L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            new NamedThreadFactory("异步任务线程池")
        );
        this.asyncExecutor.allowCoreThreadTimeOut(true);

        this.scheduler = new ScheduledThreadPoolExecutor(
            Math.max(1, processors >> 1),
            new NamedThreadFactory("定时任务线程池")
        );
        this.scheduler.setRemoveOnCancelPolicy(true);
    }

    @Override
    public Future<?> submit(Runnable task) {
        assertRunning();
        String taskId = nextTaskId();
        FutureTask<?> futureTask = new FutureTask<>(wrapOnce(taskId, task), null) {
            @Override
            protected void done() {
                taskMap.remove(taskId);
            }
        };
        taskMap.put(taskId, new ManagedTask(TaskType.ASYNC, futureTask));
        asyncExecutor.execute(futureTask);
        return futureTask;
    }

    @Override
    public <T> String submit(Callable<T> callable) {
        assertRunning();
        String taskId = nextTaskId();
        FutureTask<T> futureTask = new FutureTask<>(wrapOnce(taskId, callable));
        taskMap.put(taskId, new ManagedTask(TaskType.ASYNC, futureTask));
        asyncExecutor.execute(futureTask);
        return taskId;
    }

    @Override
    public String schedule(Runnable runnable, long delay, TimeUnit timeUnit) {
        assertRunning();
        validateDelay(delay, timeUnit);
        String taskId = nextTaskId();
        ScheduledFuture<?> future = scheduler.schedule(wrapOnce(taskId, runnable), delay, timeUnit);
        taskMap.put(taskId, new ManagedTask(TaskType.DELAYED, future));
        return taskId;
    }

    @Override
    public String scheduleAt(Runnable runnable, Instant executeAt) {
        if (executeAt == null) {
            throw new IllegalArgumentException("executeAt 不能为空");
        }
        long delay = Math.max(0L, Duration.between(Instant.now(), executeAt).toMillis());
        return schedule(runnable, delay, TimeUnit.MILLISECONDS);
    }

    @Override
    public String scheduleAtFixedRate(Runnable runnable, long initialDelay, long period, TimeUnit timeUnit) {
        assertRunning();
        validatePeriodic(initialDelay, period, timeUnit);
        String taskId = nextTaskId();
        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(wrapPeriodic(taskId, runnable),
            initialDelay, period, timeUnit);
        taskMap.put(taskId, new ManagedTask(TaskType.FIXED_RATE, future));
        return taskId;
    }

    @Override
    public String scheduleWithFixedDelay(Runnable runnable, long initialDelay, long delay, TimeUnit timeUnit) {
        assertRunning();
        validatePeriodic(initialDelay, delay, timeUnit);
        String taskId = nextTaskId();
        ScheduledFuture<?> future = scheduler.scheduleWithFixedDelay(wrapPeriodic(taskId, runnable),
            initialDelay, delay, timeUnit);
        taskMap.put(taskId, new ManagedTask(TaskType.FIXED_DELAY, future));
        return taskId;
    }

    @Override
    public boolean cancel(String taskId) {
        if (taskId == null) {
            return true;
        }
        ManagedTask task = taskMap.remove(taskId);
        if (task == null) {
            return true;
        }
        return task.future.cancel(false);
    }

    @Override
    public boolean contains(String taskId) {
        return taskId != null && taskMap.containsKey(taskId);
    }

    @Override
    public int getTaskCount() {
        return taskMap.size();
    }

    @Override
    public Set<String> getTaskIds() {
        return Collections.unmodifiableSet(taskMap.keySet());
    }

    @Override
    public void setAsyncPoolSize(int poolSize) {
        if (poolSize < 1) {
            throw new IllegalArgumentException("poolSize 必须大于 0");
        }
        if (poolSize > asyncExecutor.getMaximumPoolSize()) {
            asyncExecutor.setMaximumPoolSize(poolSize);
        }
        asyncExecutor.setCorePoolSize(poolSize);
    }

    @Override
    public void setSchedulerPoolSize(int poolSize) {
        if (poolSize < 1) {
            throw new IllegalArgumentException("poolSize 必须大于 0");
        }
        scheduler.setCorePoolSize(poolSize);
    }

    @Override
    public void close() {
        if (!closed.compareAndSet(false, true)) {
            return;
        }
        taskMap.forEach((taskId, task) -> task.future.cancel(false));
        taskMap.clear();
        asyncExecutor.shutdown();
        scheduler.shutdown();
    }

    private Runnable wrapOnce(String taskId, Runnable runnable) {
        if (runnable == null) {
            throw new IllegalArgumentException("runnable 不能为空");
        }
        ContextSnapshot snapshot = captureContext();
        return () -> {
            List<Object> backups = installContext(snapshot);
            try {
                runnable.run();
            } finally {
                try {
                    taskMap.remove(taskId);
                } finally {
                    restoreContext(backups);
                }
            }
        };
    }

    private <T> Callable<T> wrapOnce(String taskId, Callable<T> callable) {
        if (callable == null) {
            throw new IllegalArgumentException("callable 不能为空");
        }
        ContextSnapshot snapshot = captureContext();
        return () -> {
            List<Object> backups = installContext(snapshot);
            try {
                return callable.call();
            } finally {
                try {
                    taskMap.remove(taskId);
                } finally {
                    restoreContext(backups);
                }
            }
        };
    }

    private Runnable wrapPeriodic(String taskId, Runnable runnable) {
        if (runnable == null) {
            throw new IllegalArgumentException("runnable 不能为空");
        }
        ContextSnapshot snapshot = captureContext();
        return () -> {
            List<Object> backups = installContext(snapshot);
            try {
                runnable.run();
            } catch (RuntimeException | Error ex) {
                taskMap.remove(taskId);
                throw ex;
            } finally {
                restoreContext(backups);
            }
        };
    }

    private ContextSnapshot captureContext() {
        if (propagators.isEmpty()) {
            return ContextSnapshot.empty();
        }
        List<Object> snapshots = new ArrayList<>(propagators.size());
        for (AsyncTaskContextPropagator propagator : propagators) {
            snapshots.add(propagator.capture());
        }
        return new ContextSnapshot(snapshots);
    }

    private List<Object> installContext(ContextSnapshot snapshot) {
        if (propagators.isEmpty()) {
            return Collections.emptyList();
        }
        List<Object> backups = new ArrayList<>(Collections.nCopies(propagators.size(), null));
        int installed = 0;
        try {
            for (int i = 0; i < propagators.size(); i++) {
                backups.set(i, propagators.get(i).install(snapshot.get(i)));
                installed = i + 1;
            }
            return backups;
        } catch (RuntimeException | Error ex) {
            restoreInstalled(backups, installed);
            throw ex;
        }
    }

    private void restoreContext(List<Object> backups) {
        if (propagators.isEmpty() || backups.isEmpty()) {
            return;
        }
        restoreInstalled(backups, backups.size());
    }

    private void restoreInstalled(List<Object> backups, int installed) {
        for (int i = installed - 1; i >= 0; i--) {
            propagators.get(i).restore(backups.get(i));
        }
    }

    private void assertRunning() {
        if (closed.get()) {
            throw new IllegalStateException("AsyncTaskExcutor 已关闭");
        }
    }

    private void validateDelay(long delay, TimeUnit timeUnit) {
        if (delay < 0) {
            throw new IllegalArgumentException("delay 不能小于 0");
        }
        if (timeUnit == null) {
            throw new IllegalArgumentException("timeUnit 不能为空");
        }
    }

    private void validatePeriodic(long initialDelay, long period, TimeUnit timeUnit) {
        if (initialDelay < 0) {
            throw new IllegalArgumentException("initialDelay 不能小于 0");
        }
        if (period <= 0) {
            throw new IllegalArgumentException("period 必须大于 0");
        }
        if (timeUnit == null) {
            throw new IllegalArgumentException("timeUnit 不能为空");
        }
    }

    private String nextTaskId() {
        return IdUtils.random32();
    }

    private enum TaskType {
        ASYNC,
        DELAYED,
        FIXED_RATE,
        FIXED_DELAY
    }

    private static class ManagedTask {

        private final TaskType taskType;

        private final Future<?> future;

        private ManagedTask(TaskType taskType, Future<?> future) {
            this.taskType = taskType;
            this.future = future;
        }
    }

    private static final class ContextSnapshot {

        private static final ContextSnapshot EMPTY = new ContextSnapshot(Collections.emptyList());

        private final List<Object> snapshots;

        private ContextSnapshot(List<Object> snapshots) {
            this.snapshots = snapshots;
        }

        private static ContextSnapshot empty() {
            return EMPTY;
        }

        private Object get(int index) {
            return snapshots.get(index);
        }
    }
}
