package org.athena.framework.data.mybatis.builder.rewrite;

import com.alibaba.druid.sql.ast.SQLStatement;

import java.util.Map;

public interface SqlRewriteEngine {

    /**
     * Enhances the provided SQL statement by adding new columns and their corresponding values.
     *
     * @param statement  the SQL statement to be enhanced
     * @param newColumns a map where keys are the names of the new columns and values are the corresponding values to be added
     * @return the enhanced SQL statement as a String
     */
    String enhance(SQLStatement statement, Map<String, Object> newColumns);

}
