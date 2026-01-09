package org.athena.framework.data.jpa.utils;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.arthena.framework.common.exception.TodoException;
import org.athena.framework.data.jdbc.req.BaseRequest;
import org.athena.framework.data.jdbc.req.FiledQuery;
import org.athena.framework.data.jdbc.req.Sort;
import org.athena.framework.data.jdbc.type.QueryType;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
public class JpaQueryEngineUtils {

    public static <T> Specification<T> build(BaseRequest req) {
        doProcessClass(req);
        return (root, query, cb) -> {
            List<Predicate> ps = new ArrayList<>();
            for (FiledQuery f : req.getFiledQueries()) {
                Path<?> path = root.get(f.getFiledName());
                switch (f.getType()) {
                    case EQ -> ps.add(cb.equal(path, f.getValue()));
                    case NE -> ps.add(cb.notEqual(path, f.getValue()));
                    case GT -> ps.add(cb.greaterThan(path.as(Comparable.class), (Comparable) f.getValue()));
                    case GE -> ps.add(cb.greaterThanOrEqualTo(path.as(Comparable.class), (Comparable) f.getValue()));
                    case LT -> ps.add(cb.lessThan(path.as(Comparable.class), (Comparable) f.getValue()));
                    case LE -> ps.add(cb.lessThanOrEqualTo(path.as(Comparable.class), (Comparable) f.getValue()));
                    case LIKE -> ps.add(cb.like(path.as(String.class), "%" + f.getValue() + "%"));
                    case IN -> ps.add(path.in((Collection<?>) f.getValue()));
                    case IS_NULL -> ps.add(cb.isNull(path));
                    case IS_NOT_NULL -> ps.add(cb.isNotNull(path));
                    default -> throw new TodoException();
                }
            }

            // sort
            for (Sort s : req.getSorts()) {
                query.orderBy(s.isAsc() ? cb.asc(root.get(s.getColumn()))
                        : cb.desc(root.get(s.getColumn())));
            }

            return cb.and(ps.toArray(new Predicate[0]));
        };
    }

    private static void doProcessClass(BaseRequest query) {
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
                    String fieldName = field.getName();
                    if (value == null) {
                        continue;
                    }
//                    String fieldName = CamelCaseUtils.firstLowerCase(field.getName());

                    query.getFiledQueries().add(FiledQuery.builder()
                            .filedName(fieldName)
                            .type(QueryType.EQ)
                            .value(value)
                            .build());
                } catch (IllegalAccessException e) {
                    LOGGER.warn("", e);
                } finally {
                    field.setAccessible(false);
                }

            }
            clazz = clazz.getSuperclass();
        }

    }

}
