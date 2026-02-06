package org.athena.framework.websocket.support;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryResumeStore implements ResumeStore {

    private final Map<String, SessionSnapshot> store = new ConcurrentHashMap<>();
    private final long ttlMs;

    public InMemoryResumeStore(long ttlMs) {
        this.ttlMs = ttlMs;
    }

    @Override
    public void save(SessionSnapshot snapshot) {
        if (snapshot == null || snapshot.getResumeId() == null) {
            return;
        }
        store.put(snapshot.getResumeId(), snapshot);
    }

    @Override
    public SessionSnapshot get(String resumeId) {
        if (resumeId == null) {
            return null;
        }
        SessionSnapshot snapshot = store.get(resumeId);
        if (snapshot == null) {
            return null;
        }
        long now = System.currentTimeMillis();
        // 过期直接淘汰
        if (now - snapshot.getLastSeenAt() > ttlMs) {
            store.remove(resumeId);
            return null;
        }
        return snapshot;
    }

    @Override
    public void remove(String resumeId) {
        if (resumeId != null) {
            store.remove(resumeId);
        }
    }

    @Override
    public void purgeExpired() {
        long now = System.currentTimeMillis();
        for (Map.Entry<String, SessionSnapshot> entry : store.entrySet()) {
            if (now - entry.getValue().getLastSeenAt() > ttlMs) {
                store.remove(entry.getKey());
            }
        }
    }
}
