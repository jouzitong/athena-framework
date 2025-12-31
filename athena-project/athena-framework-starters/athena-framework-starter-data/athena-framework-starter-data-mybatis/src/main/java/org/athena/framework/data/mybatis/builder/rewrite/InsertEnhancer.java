package org.athena.framework.data.mybatis.builder.rewrite;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InsertEnhancer implements SqlRewriteEngine {

    @Override
    public String enhance(SQLStatement statement, Map<String, Object> newColumns) {
        if (CollectionUtils.isEmpty(newColumns)) {
            return statement.toString();
        }
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
