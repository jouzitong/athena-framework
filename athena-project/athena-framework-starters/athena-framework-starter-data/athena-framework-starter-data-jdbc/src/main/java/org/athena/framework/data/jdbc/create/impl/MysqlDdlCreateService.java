package org.athena.framework.data.jdbc.create.impl;

import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import org.athena.framework.data.jdbc.create.BaseDdlCreateService;
import org.athena.framework.data.jdbc.create.bean.ClassTableInfo;
import org.athena.framework.data.jdbc.create.bean.DbTableColumn;
import org.athena.framework.data.jdbc.utils.CamelCaseUtils;
import org.athena.framework.data.jdbc.utils.JdbcUtils;

import java.lang.reflect.Field;
import java.sql.Statement;
import java.util.Map;

/**
 * @author zhouzhitong
 * @since 2025/7/17
 **/
@Slf4j
public class MysqlDdlCreateService extends BaseDdlCreateService {

    @Override
    protected String getHeaderTable(ClassTableInfo tableInfo) {
        // create if not exists  table_name
        Table table = tableInfo.getTable();
        String tableName = table.name();
        String tableNameCamelCase = CamelCaseUtils.toCamelCase(tableName);
        return "create table if not exists " + tableNameCamelCase;
    }

    @Override
    protected String getAllColumnDdl(ClassTableInfo tableInfo) {
        StringBuilder sb = new StringBuilder();
        for (Field field : tableInfo.getColumns()) {
            String fieldDdl = JdbcUtils.getColumnDdl(field, jdbcProperties.getType());
            sb.append(fieldDdl).append(",\n");
        }
        return sb.toString();
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
