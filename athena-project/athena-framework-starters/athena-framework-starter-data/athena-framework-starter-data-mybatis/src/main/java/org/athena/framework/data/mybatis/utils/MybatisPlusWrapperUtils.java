package org.athena.framework.data.mybatis.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.arthena.framework.common.exception.TodoException;
import org.athena.framework.data.jdbc.constant.BaseEntityConstant;
import org.athena.framework.data.jdbc.req.BaseRequest;
import org.athena.framework.data.jdbc.req.FiledQuery;
import org.athena.framework.data.jdbc.req.Sort;
import org.athena.framework.data.jdbc.type.QueryType;
import org.athena.framework.data.jdbc.utils.CamelCaseUtils;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author zhouzhitong
 * @since 2023/2/13
 */
@Service
@Slf4j
public class MybatisPlusWrapperUtils {

    public static <T> QueryWrapper<T> simpleQuery() {
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        wrapper.eq(BaseEntityConstant.DELETED, 0);
        return wrapper;
    }

    public static <T> QueryWrapper<T> simpleQuery(BaseRequest query) {
        QueryWrapper<T> wrapper = simpleQuery();
        return buildQueryWrapper(wrapper, query);
    }

    private static <T> QueryWrapper<T> buildQueryWrapper(QueryWrapper<T> wrapper, BaseRequest query) {
        if (query == null) {
            return wrapper;
        }

        List<Sort> sorts = query.getSorts();
        sorts.forEach(sort -> {
            if (sort.isAsc()) {
                wrapper.orderByAsc(sort.getColumn());
            } else {
                wrapper.orderByDesc(sort.getColumn());
            }
        });

        // 处理类中字段的查询条件
        doProcessClass(wrapper, query);

        List<FiledQuery> fieldQueries = query.getFiledQueries();
        fieldQueries.forEach(filedQuery -> {
            String fieldName = filedQuery.getFiledName();
            Object value = filedQuery.getValue();
            QueryType type = filedQuery.getType();

            switch (type) {
                case EQ -> wrapper.eq(fieldName, value);
                case NE -> wrapper.ne(fieldName, value);
                case GT -> wrapper.gt(fieldName, value);
                case GE -> wrapper.ge(fieldName, value);
                case LT -> wrapper.lt(fieldName, value);
                case LE -> wrapper.le(fieldName, value);
                case LIKE -> wrapper.like(fieldName, value);
                case IN -> wrapper.in(fieldName, value);
                case NOT_IN -> wrapper.notIn(fieldName, value);
                case IS_NULL -> wrapper.isNull(fieldName);
                case IS_NOT_NULL -> wrapper.isNotNull(fieldName);
                default -> throw new TodoException();
            }
        });

        return wrapper;
    }

    private static <T> void doProcessClass(QueryWrapper<T> wrapper, BaseRequest query) {
        Class<?> clazz = query.getClass();
        while (clazz != BaseRequest.class) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.isSynthetic() || field.getName().contains("serialVersionUID")) {
                    continue;
                }
                field.setAccessible(true);
                try {
                    Object value = field.get(query);
                    String fieldName = CamelCaseUtils.firstLowerCase(field.getName());
                    wrapper.eq(fieldName, value);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            }
            clazz = clazz.getSuperclass();
        }


    }

}

