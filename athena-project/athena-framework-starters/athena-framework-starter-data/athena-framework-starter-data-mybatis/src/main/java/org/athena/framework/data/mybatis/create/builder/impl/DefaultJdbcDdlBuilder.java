package org.athena.framework.data.mybatis.create.builder.impl;

import lombok.NoArgsConstructor;
import org.athena.framework.data.jdbc.type.DbType;
import org.athena.framework.data.mybatis.bean.TableMeta;
import org.athena.framework.data.mybatis.create.builder.IJdbcDdlBuilder;
import org.athena.framework.data.mybatis.utils.MysqlJdbcDdlUtils;

@NoArgsConstructor
public class DefaultJdbcDdlBuilder implements IJdbcDdlBuilder {

    protected DbType dbType;

    protected TableMeta newTableMeta;

    protected TableMeta oldTableMeta;

    @Override
    public IJdbcDdlBuilder setDbType(DbType dbType) {
        this.dbType = dbType;
        return this;
    }

    @Override
    public IJdbcDdlBuilder setNewTableMeta(TableMeta tableMeta) {
        this.newTableMeta = tableMeta;
        return this;
    }

    @Override
    public IJdbcDdlBuilder setOldTableMeta(TableMeta tableMeta) {
        this.oldTableMeta = tableMeta;
        return this;
    }

    @Override
    public String buildCreate() {
        return MysqlJdbcDdlUtils.genCreateDdlSql(this.newTableMeta);
    }

    @Override
    public String buildAlter() {
        return MysqlJdbcDdlUtils.genUpdateDdlSql(this.newTableMeta, this.oldTableMeta);
    }
}
