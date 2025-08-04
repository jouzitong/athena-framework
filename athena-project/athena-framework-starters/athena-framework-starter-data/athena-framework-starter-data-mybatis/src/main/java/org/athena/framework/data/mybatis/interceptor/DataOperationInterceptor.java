package org.athena.framework.data.mybatis.interceptor;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import lombok.SneakyThrows;
import org.apache.ibatis.reflection.MetaObject;
import org.arthena.framework.common.context.SystemContext;
import org.arthena.framework.common.service.IUserContextService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.athena.framework.data.jdbc.constant.BaseEntityConstant.*;

/**
 * 自动插入的数据包括：创建人、创建时间、修改人、修改时间
 *
 * @author liao
 */
public class DataOperationInterceptor implements MetaObjectHandler {

    @Autowired(required = false)
    private IUserContextService userContextService;

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

    @Override
    public TableInfo findTableInfo(MetaObject metaObject) {
        return TableInfoHelper.getTableInfo(metaObject.getOriginalObject().getClass());
    }

    @SneakyThrows
    private void checkTableField(MetaObject metaObject) {
        // FIXME Empty
    }

    private Long getCurrentUserId() {
        if (userContextService != null) {
            return Optional.ofNullable(userContextService.getUserId()).orElse(0L);

        }
        return SystemContext.DEFAULT_OPERATOR;
    }

}
