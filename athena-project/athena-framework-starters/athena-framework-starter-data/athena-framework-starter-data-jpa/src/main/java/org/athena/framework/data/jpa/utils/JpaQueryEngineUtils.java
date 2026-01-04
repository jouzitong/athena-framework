package org.athena.framework.data.jpa.utils;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.athena.framework.data.jdbc.req.BaseRequest;
import org.athena.framework.data.jdbc.req.FiledQuery;
import org.athena.framework.data.jdbc.req.Sort;
import org.athena.framework.data.jdbc.type.QueryType;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JpaQueryEngineUtils {

    public static <T> Specification<T> build(BaseRequest req) {
        doProcessClass(req);
        return (root, query, cb) -> {
            List<Predicate> ps = new ArrayList<>();
            for (FiledQuery f : req.getFiledQueries()) {
                Path<?> path = root.get(f.getFiledName());
                switch (f.getType()) {
                    case EQ -> ps.add(cb.equal(path, f.getValue()));
                    case LIKE -> ps.add(cb.like(path.as(String.class), "%" + f.getValue() + "%"));
                    case GT -> ps.add(cb.greaterThan(path.as(Comparable.class), (Comparable) f.getValue()));
                    case IN -> ps.add(path.in((Collection<?>) f.getValue()));
                    case IS_NULL -> ps.add(cb.isNull(path));
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
//                    String fieldName = CamelCaseUtils.firstLowerCase(field.getName());

                    query.getFiledQueries().add(FiledQuery.builder()
                            .filedName(fieldName)
                            .type(QueryType.EQ)
                            .value(value)
                            .build());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            }
            clazz = clazz.getSuperclass();
        }

    }

}
