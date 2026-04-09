package org.athena.framework.security.api.spi;

import org.athena.framework.security.api.model.AuditRecord;

/**
 * 审计发布器扩展点。
 * 负责输出或持久化标准化审计记录。
 */
public interface AuditPublisher {

    void publish(AuditRecord record);
}
