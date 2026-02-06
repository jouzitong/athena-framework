package org.athena.framework.websocket.support;

public interface ResumeStore {

    void save(SessionSnapshot snapshot);

    SessionSnapshot get(String resumeId);

    void remove(String resumeId);

    void purgeExpired();
}
