package org.athena.framework.data.mybatis.create.impl;

import lombok.extern.slf4j.Slf4j;
import org.athena.framework.data.mybatis.create.BaseDdlCreateService;
import org.athena.framework.data.mybatis.create.bean.ClassTableInfo;
import org.athena.framework.data.mybatis.create.bean.DbTableColumn;
import org.athena.framework.data.mybatis.utils.JdbcUtils;

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
    protected String getAllColumnDdl(ClassTableInfo tableInfo) {
        StringBuilder sb = new StringBuilder();
        int lastIndex = tableInfo.getColumns().size() - 1;
        for (int i = 0; i < tableInfo.getColumns().size(); i++) {
            Field field = tableInfo.getColumns().get(i);
            String fieldDdl = JdbcUtils.getColumnDdl(field, jdbcProperties.getType());
            if (i == lastIndex) {
                sb.append(fieldDdl).append("\n");
            } else {
                sb.append(fieldDdl).append(",\n");
            }
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
