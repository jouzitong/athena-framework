package org.athena.framework.data.mybatis.builder;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import jakarta.persistence.Embedded;
import org.arthena.framework.common.utils.ClassUtils;
import org.athena.framework.data.mybatis.bean.meta.ColumnMeta;
import org.athena.framework.data.mybatis.interceptor.EmbeddedInterceptor;
import org.athena.framework.data.mybatis.utils.EmbeddedPrefixUtils;
import org.athena.framework.data.mybatis.utils.TableFieldParseUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmbeddedSqlBuilder {

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
        SQLStatement statement = SQLUtils.parseSingleStatement(sql, DbType.mysql);
        SQLInsertStatement insert = (SQLInsertStatement) statement;

        // 1. 字段列表
        List<SQLExpr> columns = insert.getColumns();
        // 2. VALUES 列表
        SQLInsertStatement.ValuesClause values = insert.getValuesList().get(0);
        List<SQLExpr> valueExprs = values.getValues();

        for (Map.Entry<String, Object> entry : newColumns.entrySet()) {
            columns.add(new SQLIdentifierExpr(entry.getKey()));
//            valueExprs.add(new SQLIdentifierExpr(entry.getValue())); // 一般是 "?" 或 SQLExpr
            valueExprs.add(new SQLIdentifierExpr("?")); // 一般是 "?" 或 SQLExpr
        }

        return SQLUtils.toSQLString(insert, DbType.mysql);
    }


}
