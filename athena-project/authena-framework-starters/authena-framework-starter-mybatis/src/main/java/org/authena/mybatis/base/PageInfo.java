package org.authena.mybatis.base;

import lombok.Getter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * 分页描述信息
 *
 * @author zhouzhitong
 * @version 1.0
 * @since 2022/6/18
 */
@Getter
@ToString
public class PageInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 总共记录条数
     */
    private final Long total;

    /**
     * 页面大小
     */
    private final Long size;

    /**
     * 当前页数
     */
    private final Long currentPage;

    public PageInfo(long total, long size, long currentPage) {
        this.total = total;
        this.size = size;
        this.currentPage = currentPage;
    }


}
