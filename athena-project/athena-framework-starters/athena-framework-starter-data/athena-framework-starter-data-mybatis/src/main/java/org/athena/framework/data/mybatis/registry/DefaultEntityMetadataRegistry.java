package org.athena.framework.data.mybatis.registry;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.arthena.framework.common.utils.PackageUtil;
import org.athena.framework.data.jdbc.entity.IEntity;
import org.athena.framework.data.jdbc.properties.DefaultJdbcProperties;
import org.athena.framework.data.mybatis.bean.TableMeta;
import org.athena.framework.data.mybatis.create.builder.ITableMetaBuilder;
import org.athena.framework.data.mybatis.create.builder.impl.DefaultTableMetaBuilder;
import org.athena.framework.data.mybatis.create.parser.ITableMetaParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class DefaultEntityMetadataRegistry implements IEntityMetadataRegistry, CommandLineRunner {

    private final Map<Class<?>, TableMeta> ENTITY_TABLE_MAP = new ConcurrentHashMap<>();

    @Resource
    protected DefaultJdbcProperties jdbcProperties;

    @Autowired(required = false)
    private List<ITableMetaParser> parsers;

    private final ITableMetaBuilder tableBuilder = new DefaultTableMetaBuilder();

    @Override
    public void run(String... args) throws Exception {
        if (CollectionUtils.isNotEmpty(parsers)) {
            for (ITableMetaParser parser : parsers) {
                tableBuilder.addParser(parser);
            }
        }
        List<Class<IEntity>> subClasses = getSubClasses(IEntity.class);
        for (Class<?> clazz : subClasses) {
            register(clazz);
        }
    }

    @Override
    public TableMeta register(Class<?> entityClass) {
        tableBuilder.clazz(entityClass);
        TableMeta tableMeta = tableBuilder.build();
        ENTITY_TABLE_MAP.put(entityClass, tableMeta);
        return tableMeta;
    }

    @Override
    public TableMeta getTableMeta(Class<?> entityClass) {
        return ENTITY_TABLE_MAP.get(entityClass);
    }

    protected <T> List<Class<T>> getSubClasses(Class<T> clazz) {
        return PackageUtil.getSubClasses(clazz, jdbcProperties.getBaseEntityPackages());
    }
}
