package org.arthena.framework.common.lock;

/**
 * 加锁服务接口
 *
 * @author zhouzhitong
 * @since 2024/10/20
 **/
public interface LockService {

    /**
     * 尝试获取锁的方法
     *
     * @param key     锁的键
     * @param value   锁的值（建议使用唯一标识，比如当前线程 ID）
     * @param timeout 锁的超时时间（单位：秒）
     * @return 是否获取到锁
     */
    boolean tryLock(String key, String value, long timeout);

    /**
     * 释放锁的方法
     *
     * @param key   锁的键
     * @param value 锁的值（确保只释放自己持有的锁）
     * @return 是否释放成功
     */
    boolean unlock(String key, String value);

    /**
     * 判断锁是否已存在的方法
     *
     * @param key 锁的键
     * @return 是否存在锁
     */
    boolean isLocked(String key);

    /**
     * 释放指定键的锁。(这个是预留的方法, 不建议业务直接使用)
     *
     * @param key 锁的键
     * @return 如果成功释放了锁，则返回true；否则返回false
     */
    boolean unlock(String key);


    /**
     * 清除所有锁。
     * <p>
     * 该方法会移除当前服务中所有的锁，无论这些锁的状态如何。此操作应谨慎使用，因为它可能会影响正在运行的业务逻辑。
     * 建议在系统维护或重启时调用此方法以确保所有锁都被正确释放。
     */
    void clearAllLock();

}
