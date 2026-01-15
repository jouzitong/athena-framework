package org.athena.framework.data.jdbc.serivce;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 *
 * @author zhouzhitong
 * @since 2026/1/15
 */
@Service
@Slf4j
public class EmptyJdbcCrudInterceptor implements IJdbcCrudInterceptor {

    @Override
    public Class<?> entityType() {
        return Object.class;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
