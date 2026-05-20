package org.athena.framework.data.jdbc.vo;

import java.io.Serial;
import java.io.Serializable;

/**
 * 分页描述信息
 *
 * @param total       总共记录条数
 * @param size        页面大小
 * @param currentPage 当前页数
 * @author zhouzhitong
 * @version 1.0
 * @since 2022/6/18
 */
public record PageInfo(Long total, Integer size, Integer currentPage) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;


}
