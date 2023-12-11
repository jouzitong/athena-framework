package org.authena.mybatis.anno;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 表字段描述
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
     * @return 字段描述
     */
    @AliasFor("comment")
    String value() default "";

    /**
     * 字段描述
     * @return 字段描述
     */
    @AliasFor("value")
    String comment() default "";

}
