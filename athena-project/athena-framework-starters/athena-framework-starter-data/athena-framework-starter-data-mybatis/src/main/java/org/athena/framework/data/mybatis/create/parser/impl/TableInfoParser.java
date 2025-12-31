package org.athena.framework.data.mybatis.create.parser.impl;

import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.Table;
import org.apache.commons.lang3.StringUtils;
import org.athena.framework.data.mybatis.bean.TableMeta;
import org.athena.framework.data.mybatis.create.parser.ITableMetaParser;
import org.springframework.core.Ordered;

/**
 *
 */
public class TableInfoParser implements ITableMetaParser {

    @Override
    public boolean parse(Class<?> clazz, TableMeta tableMeta) {
        // 解析表定义信息
        parseTableInfo(clazz, tableMeta);
        return true;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    /**
     * 解析给定类中的表信息，并填充到TableMeta对象中。
     *
     * @param clazz     用于解析表元数据的类
     * @param tableMeta 表定义信息，将被填充解析出的数据
     */
    private void parseTableInfo(Class<?> clazz, TableMeta tableMeta) {
        Table table = clazz.getAnnotation(Table.class);
        TableName mp_table = clazz.getAnnotation(TableName.class);
        String tableName = null;
        if (table != null) {
            tableName = table.name();
        }
        if (mp_table != null) {
            tableName = mp_table.value();
        }
        if (StringUtils.isNotBlank(tableName)) {
            tableMeta.setName(tableName);
            tableMeta.setComment(tableName);
        }
    }

}
