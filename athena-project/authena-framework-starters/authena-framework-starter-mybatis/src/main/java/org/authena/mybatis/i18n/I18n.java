package org.authena.mybatis.i18n;

import java.lang.annotation.*;

/**
 * 放在 entity 类上, 表示该类需要国际化支持.
 *
 * @author zhouzhitong
 * @version 1.0
 * @since 2022/5/12 23:46
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface I18n {
}
