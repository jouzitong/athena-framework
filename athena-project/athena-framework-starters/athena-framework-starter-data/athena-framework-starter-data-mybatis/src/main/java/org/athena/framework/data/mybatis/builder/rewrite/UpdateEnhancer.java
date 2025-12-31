package org.athena.framework.data.mybatis.builder.rewrite;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class UpdateEnhancer implements SqlRewriteEngine {

    @Override
    public String enhance(SQLStatement statement, Map<String, Object> newColumns) {
        SQLUpdateStatement stmt = (SQLUpdateStatement) statement;
        List<SQLUpdateSetItem> items = stmt.getItems();
        newColumns.forEach((key, value) -> {
            SQLUpdateSetItem item = new SQLUpdateSetItem();
            item.setColumn(new SQLIdentifierExpr("key"));
            item.setValue(new SQLVariantRefExpr("?"));
            items.add(item);
        });
        return SQLUtils.toSQLString(stmt, DbType.mysql);
    }
}
