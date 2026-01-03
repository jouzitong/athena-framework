package org.athena.framework.data.mybatis.registry;

import org.athena.framework.data.mybatis.bean.TableMeta;

public interface IEntityMetadataRegistry {

    TableMeta register(Class<?> entityClass);

    TableMeta getTableMeta(Class<?> entityClass);

}
