package org.authena.mybatis.autoGen.config;

/**
 * 数据库字段类型映射配置
 *
 * @author zhouzhitong
 * @since 2023-11-14
 **/
public interface DatabaseTypeMapConfig {

    /**
     * 获取字段类型映射
     *
     * @param c 字段类型
     * @return 字段类型映射
     */
    FieldTypeMap get(Class<?> c);

}
