package org.athena.framework.data.jdbc.entity.dto;

/**
 * @author zhouzhitong
 * @since 2025/7/6
 **/
public interface IDTO {

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
