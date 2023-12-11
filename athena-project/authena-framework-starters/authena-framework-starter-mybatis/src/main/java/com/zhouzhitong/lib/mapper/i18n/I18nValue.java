package com.zhouzhitong.lib.mapper.i18n;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
