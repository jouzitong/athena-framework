package org.athena.framework.mybatis.interceptor;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.SneakyThrows;
import org.apache.ibatis.reflection.MetaObject;
import org.arthena.framework.common.context.SystemContext;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.athena.framework.mybatis.constant.BaseEntityConstant.*;

/**
 * 自动插入的数据包括：创建人、创建时间、修改人、修改时间
 *
 * @author liao
 */
public class DataOperationInterceptor implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, CREATE_TIME, LocalDateTime::now, LocalDateTime.class);
        this.strictInsertFill(metaObject, UPDATE_TIME, LocalDateTime::now, LocalDateTime.class);
        this.strictInsertFill(metaObject, CREATE_BY, this::getCurrentUserId, Long.class);
        this.strictInsertFill(metaObject, DELETED, () -> 0, Integer.class);
        checkTableField(metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, UPDATE_TIME, LocalDateTime::now, LocalDateTime.class);
        this.strictUpdateFill(metaObject, LAST_MODIFIED_BY, this::getCurrentUserId, Long.class);
        checkTableField(metaObject);
    }


    @SneakyThrows
    private void checkTableField(MetaObject metaObject) {
        // FIXME Empty
    }

    private Long getCurrentUserId() {
        return Optional.ofNullable(SystemContext.getUserId()).orElse(0L);
    }

}
