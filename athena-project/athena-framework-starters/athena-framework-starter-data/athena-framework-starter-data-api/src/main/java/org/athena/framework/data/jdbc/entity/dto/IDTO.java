package org.athena.framework.data.jdbc.entity.dto;

import java.io.Serializable;

/**
 * @author zhouzhitong
 * @since 2025/7/6
 **/
public interface IDTO extends Serializable {

    Long getId();

    /**
     * 业务ID
     *
     * @return 业务ID
     */
    default String getBizId() {
        return null;
    }

}
