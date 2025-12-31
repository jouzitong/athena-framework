package org.athena.framework.data.mybatis.builder.rewrite;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class DeleteEnhancer implements SqlRewriteEngine {

    @Override
    public String enhance(SQLStatement statement, Map<String, Object> newColumns) {
        SQLDeleteStatement stmt = (SQLDeleteStatement) statement;
        newColumns.forEach((key, value) -> {
            stmt.addWhere(new SQLBinaryOpExpr(
                    new SQLIdentifierExpr(key),
                    // TODO 待优化
                    SQLBinaryOperator.Equality,
                    new SQLVariantRefExpr("?")
            ));

        });
        SQLUpdateSetItem item = new SQLUpdateSetItem();
        item.setColumn(new SQLIdentifierExpr("last_modified_by"));
        item.setValue(new SQLVariantRefExpr("?"));
//        stmt.getItems().add(item);
        return SQLUtils.toSQLString(stmt, DbType.mysql);
    }


}
