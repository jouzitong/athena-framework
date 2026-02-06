package org.athena.framework.websocket.support;

/**
 * 会话恢复快照存储
 */
public interface ResumeStore {

    /**
     * 保存会话快照
     */
    void save(SessionSnapshot snapshot);

    /**
     * 读取会话快照
     */
    SessionSnapshot get(String resumeId);

    /**
     * 删除会话快照
     */
    void remove(String resumeId);

    /**
     * 清理过期快照
     */
    void purgeExpired();
}
