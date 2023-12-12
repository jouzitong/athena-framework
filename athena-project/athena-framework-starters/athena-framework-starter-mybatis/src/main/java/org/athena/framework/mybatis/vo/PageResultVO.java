package org.athena.framework.mybatis.vo;

import lombok.Getter;
import lombok.ToString;
import org.arthena.framework.common.vo.ResultVO;
import org.athena.framework.mybatis.base.PageInfo;

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
public class PageResultVO<T> extends ResultVO<List<T>> {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 分页信息, 如果是null, 则不分页
     */
    private final PageInfo pageInfo;

    private PageResultVO(List<T> page, PageInfo pageInfo) {
        super(page);
        this.pageInfo = pageInfo;
    }

    public static <T> PageResultVO<T> ok(List<T> dataList, PageInfo pageInfo) {
        return success(dataList, pageInfo);
    }

    public static <T> PageResultVO<T> success(List<T> dataList, PageInfo pageInfo) {
        return new PageResultVO<>(dataList, pageInfo);
    }

}
