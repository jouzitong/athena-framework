package org.athena.framework.mybatis.autoGen;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.arthena.framework.common.utils.PackageUtil;
import org.athena.framework.mybatis.annotation.FieldComment;
import org.athena.framework.mybatis.entity.BaseEntity;
import org.athena.framework.mybatis.properties.DefaultMapperProperties;
import org.athena.framework.mybatis.properties.FieldTypeMap;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author zhouzhitong
 * @since 2024-03-21
 **/
@Slf4j
@AllArgsConstructor
public abstract class BaseGenService {

    protected final DefaultMapperProperties mapperProperties;

    protected final DatabaseTypeMapConfig config;

    protected final DataSource dataSource;

    protected List<Field> getAllFields(Class<?> c) {
        List<Field> allFields = new LinkedList<>();
        while (c != BaseEntity.class) {
            Field[] fields = c.getDeclaredFields();
            allFields.addAll(Arrays.asList(fields));
            c = c.getSuperclass();
        }
        return allFields;
    }

    protected List<Field> getAllFieldsAndBase(Class<?> c) {
        List<Field> allFields = new LinkedList<>();
        while (c != Model.class && c != Object.class) {
            Field[] fields = c.getDeclaredFields();

            for (Field field : fields) {
                // 过滤掉静态字段
                if (field.isSynthetic() || field.getName().contains("serialVersionUID")) {
                    continue;
                }
                allFields.add(field);
            }
            c = c.getSuperclass();
        }
        return allFields;
    }


    protected String getFieldType(Field field) {
        FieldComment fieldComment = field.getAnnotation(FieldComment.class);
        if (fieldComment != null&& !fieldComment.type().isEmpty()) {
            return fieldComment.type();
        }
        FieldTypeMap fieldTypeMap = config.get(field.getType());
        if (fieldTypeMap == null) {
            throw new RuntimeException("未知的字段类型: " + field.getClass());
        }
        Class<?> subType = getGenericTypeName(field);
        return fieldTypeMap.getType(subType);
    }

    protected String getTableName(Class<?> c) {
        TableName annotation = c.getAnnotation(TableName.class);
        if (annotation == null || StringUtils.isBlank(annotation.value())) {
            // 类名的驼峰转下划线
            String name = c.getSimpleName();
            return getUnderlineName(name);
        }
        String res = annotation.value().trim();
        if (res.startsWith("`") && res.endsWith("`")) {
            return res;
        } else {
            return "`" + res + "`";
        }
    }

    protected List<Class<?>> getSubClasses() {
        return PackageUtil.getSubClasses(BaseEntity.class, mapperProperties.getBaseEntityPackages());
    }


    /**
     * 将字符转换成驼峰命名(小写+下划线)
     */
    protected String getUnderlineName(String name) {
        StringBuilder result = new StringBuilder();
        if (name != null && name.length() > 0) {
            // 将第一个字符处理成大写
            result.append(name.substring(0, 1).toLowerCase());
            // 循环处理其余字符
            for (int i = 1; i < name.length(); i++) {
                String s = name.substring(i, i + 1);
                // 在大写字母前添加下划线
                if (s.equals(s.toUpperCase()) && !Character.isDigit(s.charAt(0))) {
                    result.append("_");
                }
                // 其他字符直接转成小写
                result.append(s.toLowerCase());
            }
        }
        return result.toString().trim();
    }

    // Ljava/util/List<Ljava/lang/Long;>;
    protected Class<?> getGenericTypeName(Field field) {
        Type genericType = field.getGenericType();
        String signature = genericType.getTypeName();
        // 如果没有 < 和 > 返回空
        if (!signature.contains("<") && !signature.contains(">")) {
            return null;
        }
        int begin = signature.indexOf("<");
        int end = signature.indexOf(">");
        String substring = signature.substring(begin + 1, end);
        try {
            return Class.forName(substring);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
