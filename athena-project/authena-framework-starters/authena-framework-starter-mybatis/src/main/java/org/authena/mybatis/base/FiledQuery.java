package org.authena.mybatis.base;

import lombok.Data;
import org.authena.mybatis.type.QueryType;

import javax.annotation.Nullable;
import java.io.Serial;
import java.io.Serializable;

/**
 * @author zhouzhitong
 * @since 2023/5/21
 */
@Data
public class FiledQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 字段名
     */
    private String filedName;

    /**
     * 字段值
     */
    private Object value;

    /**
     * 查询类型
     */
    private QueryType type = QueryType.EQ;

    public static FiledQuery of(String filedName, Object value, @Nullable QueryType type) {
        FiledQuery filedQuery = new FiledQuery();
        filedQuery.setFiledName(filedName);
        filedQuery.setValue(value);
        if (type != null) {
            filedQuery.setType(type);
        }
        return filedQuery;
    }

}
