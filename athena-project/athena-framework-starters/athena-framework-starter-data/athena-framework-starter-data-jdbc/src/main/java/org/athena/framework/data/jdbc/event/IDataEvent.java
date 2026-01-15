package org.athena.framework.data.jdbc.event;

import org.arthena.framework.common.event.IEvent;
import org.athena.framework.data.jdbc.type.DbOpType;

/**
 *
 * @author zhouzhitong
 * @since 2026/1/15
 */
public interface IDataEvent extends IEvent {

    DbOpType opType();

    Class<?> entityClazz();

    Object params();

}
