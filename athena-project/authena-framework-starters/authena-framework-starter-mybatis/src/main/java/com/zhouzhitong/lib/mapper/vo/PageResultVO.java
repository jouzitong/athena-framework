package com.zhouzhitong.lib.mapper.vo;

import com.zhouzhitong.lib.mapper.base.PageInfo;
import lombok.Getter;
import lombok.ToString;

import java.io.Serial;
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
public class PageResultVO<T>  {

    @Serial
    private static final long serialVersionUID = 1L;

    private final List<T> data;

    /**
     * 分页信息, 如果是null, 则不分页
     */
    private final PageInfo pageInfo;

    private PageResultVO(List<T> data, PageInfo pageInfo) {
        this.data = data;
        this.pageInfo = pageInfo;
    }

    public static <T> PageResultVO<T> ok(List<T> dataList, PageInfo pageInfo) {
        return success(dataList, pageInfo);
    }

    public static <T> PageResultVO<T> success(List<T> dataList, PageInfo pageInfo) {
        return new PageResultVO<>(dataList, pageInfo);
    }

}
