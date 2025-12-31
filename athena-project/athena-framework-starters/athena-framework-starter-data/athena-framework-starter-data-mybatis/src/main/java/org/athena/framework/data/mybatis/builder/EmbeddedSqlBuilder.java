package org.athena.framework.data.mybatis.builder;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import jakarta.persistence.Embedded;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.arthena.framework.common.provider.ApplicationContextProvider;
import org.arthena.framework.common.utils.ClassUtils;
import org.athena.framework.data.mybatis.bean.meta.ColumnMeta;
import org.athena.framework.data.mybatis.builder.rewrite.*;
import org.athena.framework.data.mybatis.interceptor.EmbeddedInterceptor;
import org.athena.framework.data.mybatis.utils.EmbeddedPrefixUtils;
import org.athena.framework.data.mybatis.utils.TableFieldParseUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class EmbeddedSqlBuilder {

    private static DeleteEnhancer deleteEnhancer;
    private static InsertEnhancer insertEnhancer;
    private static SelectEnhancer selectEnhancer;
    private static UpdateEnhancer updateEnhancer;

    /**
     *
     * @param sql   sql.
     *              Query: SELECT * from table where id = ? and name = ?
     *              insert: INSERT INTO table(id,name) values(?,?)
     *              update: UPDATE table set name = ? where id = ?
     *              delete: DELETE from table where id = ?
     * @param param 参数对象
     * @return FlatSql
     */
    public static EmbeddedInterceptor.FlatSql rewrite(String sql, Object param) {
        Map<String, Object> map = parseParam(param);

        if (sql.startsWith("SELECT")) {
            return processQuery(sql, map);
        } else if (sql.startsWith("INSERT")) {
            return processInsert(sql, map);
        }
        return null;
    }

    private static EmbeddedInterceptor.FlatSql processQuery(String sql, Map<String, Object> map) {

        return null;
    }

    private static EmbeddedInterceptor.FlatSql processInsert(String sql, Map<String, Object> map) {
        // INSERT INTO person  ( id, name, age, gender, create_time, update_time, created_by, last_modified_by, version ) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?  )
        String newSql = appendInsertColumns(sql, map);

        return EmbeddedInterceptor.FlatSql.builder()
                .sql(newSql)
                .params(map)
                .build();
    }

    private static Map<String, Object> parseParam(Object param) {
        // 解析 param 字段中的 @Embedded 注解
        Map<String, Object> map = new HashMap<>();
        Field[] fields = param.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(Embedded.class)) {
                continue;
            }
            Object value = ClassUtils.getFieldValue(param, field);
            if (value == null) {
                return map;
            }
            String prefix = EmbeddedPrefixUtils.getPrefix(field);
            Class<?> type = field.getType();
//            ColumnMetaParser parser = new ColumnMetaParser();
//            TableMeta tableMeta = new TableMeta();
//            parser.parse(type, tableMeta);
//            List<ColumnMeta> columns = tableMeta.getColumns();

            Field[] subFields = type.getDeclaredFields();
            for (Field subField : subFields) {
                ColumnMeta column = TableFieldParseUtils.parseField(subField);
                String columnName = prefix + column.getName();
                String columnValue = ClassUtils.getFieldValue(value, subField);
                map.put(columnName, columnValue);
            }
        }

        return map;
    }

    private static String appendInsertColumns(String sql, Map<String, Object> newColumns) {
        SQLStatement stmt = SQLUtils.parseSingleStatement(sql, DbType.mysql);
        init();
        if (stmt instanceof SQLInsertStatement) {
            return insertEnhancer.enhance(stmt, newColumns);
        } else if (stmt instanceof SQLUpdateStatement) {
            return updateEnhancer.enhance(stmt, newColumns);
        } else if (stmt instanceof SQLSelectStatement) {
            return selectEnhancer.enhance(stmt, newColumns);
        } else if (stmt instanceof SQLDeleteStatement) {
            return deleteEnhancer.enhance(stmt, newColumns);
        }
        LOGGER.error("未知SQL: {}", sql);
        return null;
    }

    private static void init() {
        if (EmbeddedSqlBuilder.selectEnhancer != null) {
            return;
        }
        List<SqlRewriteEngine> beans = ApplicationContextProvider.getBeansOfType(SqlRewriteEngine.class);
        if (CollectionUtils.isNotEmpty(beans)) {
            for (SqlRewriteEngine bean : beans) {
                if (bean.getClass().equals(DeleteEnhancer.class)) {
                    EmbeddedSqlBuilder.deleteEnhancer = (DeleteEnhancer) bean;
                } else if (bean.getClass().equals(InsertEnhancer.class)) {
                    EmbeddedSqlBuilder.insertEnhancer = (InsertEnhancer) bean;
                } else if (bean.getClass().equals(UpdateEnhancer.class)) {
                    EmbeddedSqlBuilder.updateEnhancer = (UpdateEnhancer) bean;
                } else if (bean.getClass().equals(SelectEnhancer.class)) {
                    EmbeddedSqlBuilder.selectEnhancer = (SelectEnhancer) bean;
                }
            }
        }
    }

}
