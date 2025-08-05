package org.athena.framework.data.mybatis.create.bean;

import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author zhouzhitong
 * @since 2025/7/17
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassTableInfo {

    /**
     * 目标实体类
     */
    private Class<?> clazz;

    /**
     * 表名注解
     */
    private Table table;

    private TableName mybatisPlusTable;

    /**
     * 表字段列表
     */
    private List<Field> columns;


}
