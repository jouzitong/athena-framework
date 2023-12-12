package org.athena.framework.mybatis.i18n;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 需要国际化的字段
 *
 * @author zhouzhitong
 * @version 1.0
 * @since 2022/5/12 23:46
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface I18nValue {

    @AliasFor("key")
    String value() default "no";

    @AliasFor("value")
    String key() default "no";
}
