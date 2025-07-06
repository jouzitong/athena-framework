package org.athena.framework.data.mybatis.properties.bean;

/**
 *
 * @author zhouzhitong
 * @since 2023-11-20
 **/
public interface GenTypeConfig {

     /**
      * 根据字段类型 获取数据库字段类型
      *
      * @param c 字段类型
      * @return 数据库字段类型
      */
     FieldTypeMap get(Class<?> c) ;

}
