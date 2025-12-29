package org.athena.framework.data.mybatis.builder;

import org.athena.framework.data.jdbc.type.DbType;
import org.athena.framework.data.mybatis.bean.TableMeta;

/**
 * jdbc ddl 构建器
 */
public interface IJdbcDdlBuilder {

    /**
     * @param dbType 数据库类型
     * @return this
     */
    IJdbcDdlBuilder setDbType(DbType dbType);

    /**
     * 设置 新表定义
     * @param tableMeta 新表定义
     * @return this
     */
    IJdbcDdlBuilder setNewTableMeta(TableMeta tableMeta);

    /**
     * 设置 旧表定义
     * @param tableMeta 旧表定义
     * @return this
     */
    IJdbcDdlBuilder setOldTableMeta(TableMeta tableMeta);

    /**
     * 构建 创建表的 DDL SQL
     *
     * @return 创建表的 DDL SQL
     */
    String buildCreate();

    /**
     * 构建 修改表的 DDL SQL
     * @return 修改表的 DDL SQL
     */
    String buildAlter();

}
