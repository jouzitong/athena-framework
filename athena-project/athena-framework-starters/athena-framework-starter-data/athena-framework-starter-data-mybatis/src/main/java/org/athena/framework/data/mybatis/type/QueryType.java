package org.athena.framework.data.mybatis.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import org.arthena.framework.common.base.IEnum;
import org.arthena.framework.common.utils.EnumUtils;

import java.io.Serial;

/**
 * @author zhouzhitong
 * @since 2023/5/21
 */
@Getter
public enum QueryType implements IEnum {

    /**
     * 查询类型
     * <ul>
     *     <li>EQ(1, "等于")</li>
     *     <li>NE(2, "不等于")</li>
     *     <li>GT(3, "大于")</li>
     *     <li>GE(4, "大于等于")</li>
     *     <li>LT(5, "小于")</li>
     *     <li>LE(6, "小于等于")</li>
     *     <li>LIKE(7, "模糊查询")</li>
     *     <li>IN(8, "in")</li>
     *     <li>NOT_IN(9, "not in")</li>
     *     <li>IS_NULL(10, "is null")</li>
     *     <li>IS_NOT_NULL(11, "is not null")</li>
     *     <li>...</li>
     * </ul>
     */
    EQ(1, "等于"),
    NE(2, "不等于"),
    GT(3, "大于"),
    GE(4, "大于等于"),
    LT(5, "小于"),
    LE(6, "小于等于"),
    LIKE(7, "模糊查询"),
    IN(8, "in"),
    NOT_IN(9, "not in"),
    IS_NULL(10, "is null"),
    IS_NOT_NULL(11, "is not null"),
    ;

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonValue
    private final Integer code;

    private final String desc;

    QueryType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static QueryType of(Integer code) {
        return EnumUtils.codeOf(QueryType.class, code);
    }

}
