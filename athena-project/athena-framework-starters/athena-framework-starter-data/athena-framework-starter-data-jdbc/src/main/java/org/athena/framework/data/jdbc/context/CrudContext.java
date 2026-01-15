package org.athena.framework.data.jdbc.context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.athena.framework.data.jdbc.type.DbOpType;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author zhouzhitong
 * @since 2026/1/15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrudContext {

    private DbOpType dbOpType;

    /** 实体类型 */
    private Class<?> entityType;

    /** 原始参数 */
    private Object param;

    /** 执行结果 */
    private Object result;

    /** 异常 */
    private Exception exception;

    /** 扩展上下文 */
    private Map<String, Object> attributes = new HashMap<>();

}
