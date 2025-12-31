package org.athena.framework.data.mybatis.utils;


import org.athena.framework.data.mybatis.bean.meta.ColumnMeta;

import java.lang.reflect.Field;
import java.util.List;

public class EmbeddedPrefixUtils {

    /**
     * Adds a prefix to the names of all columns in the provided list. The prefix is
     * derived from the name of the given field, with an underscore appended.
     *
     * @param field   the field whose name will be used as a prefix
     * @param columns the list of ColumnMeta objects to which the prefix will be added
     */
    public static void paddingPrefix(Field field, List<ColumnMeta> columns) {
        String prefix = getPrefix(field);
        for (ColumnMeta columnMeta : columns) {
            columnMeta.setName(prefix + columnMeta.getName());
        }
    }


    public static String getPrefix(Field field) {
        String name = field.getName();
        return name + "_";
    }

}
