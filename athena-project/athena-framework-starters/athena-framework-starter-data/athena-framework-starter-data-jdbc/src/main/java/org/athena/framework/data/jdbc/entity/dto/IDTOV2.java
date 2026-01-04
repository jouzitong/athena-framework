package org.athena.framework.data.jdbc.entity.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author zhouzhitong
 * @since 2025/7/6
 **/
@Deprecated
public interface IDTOV2<ID extends Serializable> {

    ID getId();

    LocalDateTime getCreateTime();

    LocalDateTime getUpdateTime();

    Long getCreatedBy();

    Long getLastModifiedBy();

    Long getVersion();

    default boolean isDeleted() {
        return false;
    }

    default void setDeleted(boolean deleted) {

    }


}
