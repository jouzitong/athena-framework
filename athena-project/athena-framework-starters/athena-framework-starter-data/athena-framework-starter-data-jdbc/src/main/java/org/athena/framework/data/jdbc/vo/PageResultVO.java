package org.athena.framework.data.jdbc.vo;

import lombok.Getter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 分页查询返回接口
 *
 * @author zhouzhitong
 * @version 1.0
 * @since 2022/6/18
 */
@Getter
@ToString(callSuper = true)
public class PageResultVO<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 分页信息, 如果是null, 则不分页
     */
    private final PageInfo pageInfo;

    private final List<T> list;

    private PageResultVO(List<T> list, PageInfo pageInfo) {
        this.list = list;
        this.pageInfo = pageInfo;
    }

    public static <T> PageResultVO<T> of(List<T> list, PageInfo pageInfo) {
        return new PageResultVO<>(list, pageInfo);
    }

}
