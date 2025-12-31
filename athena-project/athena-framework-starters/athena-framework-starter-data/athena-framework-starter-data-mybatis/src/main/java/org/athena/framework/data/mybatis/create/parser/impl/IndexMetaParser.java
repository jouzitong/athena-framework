package org.athena.framework.data.mybatis.create.parser.impl;

import jakarta.persistence.Id;
import org.athena.framework.data.mybatis.bean.TableMeta;
import org.athena.framework.data.mybatis.bean.meta.IndexMeta;
import org.athena.framework.data.mybatis.create.parser.ITableMetaParser;
import org.springframework.core.Ordered;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class IndexMetaParser implements ITableMetaParser {

    @Override
    public boolean parse(Class<?> clazz, TableMeta tableMeta) {
        List<IndexMeta> indexMetas = parseIndexMeta(clazz);
        tableMeta.setIndexes(indexMetas);
        return true;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    protected List<IndexMeta> parseIndexMeta(Class<?> clazz) {

        List<IndexMeta> indexMetas = new ArrayList<>();
        Class<?> tempClazz = clazz;

        while (tempClazz != Object.class) {
            Field[] allField = tempClazz.getDeclaredFields();
            for (Field field : allField) {
                if (field.isSynthetic() || field.getName().contains("serialVersionUID")) {
                    continue;
                }
                Annotation id = field.getAnnotation(Id.class);
                if (id != null) {
                    indexMetas.add(IndexMeta.builder()
                            .type("PRIMARY")
                            .columnNames(List.of(field.getName()))
                            .build());
                }
            }
            tempClazz = tempClazz.getSuperclass();
        }
        return indexMetas;
    }

}

