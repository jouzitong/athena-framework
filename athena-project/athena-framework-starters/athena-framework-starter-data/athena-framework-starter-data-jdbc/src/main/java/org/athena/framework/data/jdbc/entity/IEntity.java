package org.athena.framework.data.jdbc.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author zhouzhitong
 * @since 2025/7/6
 **/
public interface IEntity extends Serializable {

    Long getId();

    LocalDateTime getCreateTime();

    LocalDateTime getUpdateTime();

    Long getCreatedBy();

    Long getUpdatedBy();

    Long getVersion();

//    default void setDeleted(boolean deleted) {
//
//    }

    default Long getAndIncrementVersion() {
        return 0L;
    }


}
