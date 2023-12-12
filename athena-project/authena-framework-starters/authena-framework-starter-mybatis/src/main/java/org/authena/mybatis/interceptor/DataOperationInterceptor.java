package org.authena.mybatis.interceptor;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.SneakyThrows;
import org.apache.ibatis.reflection.MetaObject;
import org.arthena.common.context.SystemContext;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author liao
 */
public class DataOperationInterceptor implements MetaObjectHandler {

    private static final String CREATE_TIME = "createTime";
    private static final String CREATE_USER = "createBy";
    private static final String UPDATE_TIME = "updateTime";
    private static final String UPDATE_USER = "lastModifiedBy";
    private static final String IS_DELETED = "isDeleted";

    private Long getCurrentUserId() {
        return Optional.ofNullable(SystemContext.getUserId()).orElse(0L);
    }

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, CREATE_TIME, LocalDateTime::now, LocalDateTime.class);
        this.strictInsertFill(metaObject, UPDATE_TIME, LocalDateTime::now, LocalDateTime.class);
        this.strictInsertFill(metaObject, CREATE_USER, this::getCurrentUserId, Long.class);
        this.strictInsertFill(metaObject, IS_DELETED, () -> Boolean.FALSE, Boolean.class);
        checkTableField(metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, UPDATE_TIME, LocalDateTime::now, LocalDateTime.class);
        this.strictUpdateFill(metaObject, UPDATE_USER, this::getCurrentUserId, Long.class);
        checkTableField(metaObject);
    }


    @SneakyThrows
    private void checkTableField(MetaObject metaObject) {
        // FIXME Empty
    }
}
