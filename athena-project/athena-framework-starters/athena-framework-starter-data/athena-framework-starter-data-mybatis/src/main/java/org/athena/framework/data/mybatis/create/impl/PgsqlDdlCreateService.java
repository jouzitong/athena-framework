package org.athena.framework.data.mybatis.create.impl;

import lombok.extern.slf4j.Slf4j;
import org.athena.framework.data.mybatis.create.BaseDdlCreateService;
import org.athena.framework.data.mybatis.create.bean.ClassTableInfo;
import org.athena.framework.data.mybatis.create.bean.DbTableColumn;

import java.lang.reflect.Field;
import java.sql.Statement;
import java.util.Map;

/**
 * @author zhouzhitong
 * @since 2025/7/17
 **/
@Slf4j
public class PgsqlDdlCreateService extends BaseDdlCreateService {

    @Override
    protected String getHeaderTable(ClassTableInfo tableInfo) {
        return "";
    }

    @Override
    protected String getAllColumnDdl(ClassTableInfo tableInfo) {
        return "";
    }

    @Override
    protected String getAddColumnDdl(Field field) {
        return "";
    }

    @Override
    protected String getUpdateColumnDdl(Field field) {
        return "";
    }

    @Override
    protected String getDropColumnDdl(String columnName) {
        return "";
    }

    @Override
    protected String getAllIndexDdl(ClassTableInfo tableInfo) {
        return "";
    }

    @Override
    protected boolean isExistTable(String tableName, Statement statement) {
        return false;
    }

    @Override
    protected Map<String, DbTableColumn> getDbTableColumns(String tableName, Statement statement) {
        return Map.of();
    }
}
