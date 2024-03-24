package org.athena.framework.mybatis.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 表字段描述. 这个类在自动创建表字段时有作用
 *
 * @author zhouzhitong
 * @since 2023-11-20
 **/
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface FieldComment {

    /**
     * 字段描述
     * <p>
     * 注意: pgsql 不建议使用该值. 具体使用 type, comment, defaultValue, isAllowNull, 表达字段的描述
     *
     * @return 字段描述
     */
    @AliasFor("name")
    String value() default "";

    /**
     * 字段描述
     *
     * @return 字段描述
     */
    @AliasFor("value")
    String name() default "";

    /**
     * 数据类型
     *
     * @return 数据类型
     */
    String type() default "";

    /**
     * 字段描述
     *
     * @return 字段描述
     */
    String comment() default "";

    /**
     * 默认值
     *
     * @return 默认值
     */
    String defaultValue() default "";

    /**
     * 是否允许为空
     *
     * @return 是否允许为空
     */
    boolean isAllowNull() default true;

}
