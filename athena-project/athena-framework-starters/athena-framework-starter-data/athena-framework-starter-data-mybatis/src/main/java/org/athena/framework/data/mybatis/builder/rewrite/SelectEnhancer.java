package org.athena.framework.data.mybatis.builder.rewrite;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class SelectEnhancer implements SqlRewriteEngine {

    @Override
    public String enhance(SQLStatement statement, Map<String, Object> newColumns) {
        SQLSelectStatement stmt = (SQLSelectStatement) statement;
//        SQLSelectQueryBlock block = (SQLSelectQueryBlock) stmt.getSelect().getQuery();
//        List<SQLSelectItem> selectList = block.getSelectList();
//        for (String col : ctx.getAppendSelectColumns()) {
//            selectList.add(new SQLSelectItem(new SQLIdentifierExpr(col)));
//        }

        newColumns.forEach((key, value) -> {
            stmt.addWhere(new SQLBinaryOpExpr(
                    new SQLIdentifierExpr(key),
                    // TODO 待优化
                    SQLBinaryOperator.Equality,
                    new SQLVariantRefExpr("?")
            ));

        });
        return SQLUtils.toSQLString(statement, DbType.mysql);
    }
}
