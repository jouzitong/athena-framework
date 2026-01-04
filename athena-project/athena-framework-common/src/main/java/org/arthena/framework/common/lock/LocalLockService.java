package org.arthena.framework.common.lock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地锁实现
 *
 * @author zhouzhitong
 * @since 2025/6/26
 **/
@Service
@Slf4j
public class LocalLockService implements LockService {

    private final Map<String, String> lockMap = new ConcurrentHashMap<>();

    public LocalLockService() {
        LOGGER.info("Init LocalLockService");
    }

    @Override
    public boolean tryLock(String key, String value, long timeout) {
        LOGGER.debug("tryLock key: {}, value: {}, timeout: {}", key, value, timeout);
        if (lockMap.containsKey(key)) {
            return false;
        }
        synchronized (this) {
            if (lockMap.containsKey(key)) {
                return false;
            }
            lockMap.put(key, value);
        }
        return true;
    }

    @Override
    public boolean unlock(String key, String value) {
        LOGGER.debug("unlock key: {}, value: {}", key, value);
        if (lockMap.get(key).equals(value)) {
            lockMap.remove(key);
            return true;
        }
        return false;
    }

    @Override
    public boolean isLocked(String key) {
        LOGGER.trace("isLocked key: {}", key);
        return lockMap.containsKey(key);
    }

    @Override
    public boolean unlock(String key) {
        LOGGER.debug("unlock key: {}", key);
        if (lockMap.get(key) != null) {
            lockMap.remove(key);
            return true;
        }
        return false;
    }

    @Override
    public void clearAllLock() {

    }
}