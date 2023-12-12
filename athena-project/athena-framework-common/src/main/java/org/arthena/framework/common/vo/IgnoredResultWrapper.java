package org.arthena.framework.common.vo;


import java.lang.annotation.*;

/**
 * 忽略返回值包装或者解包
 * 1.在接口方法上添加此注解，可以忽略返回值的包装
 * 2.在类上添加此注解，可以忽略调用第三方Feign的时候自动解包
 *
 * @author liao
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.METHOD})
public @interface IgnoredResultWrapper {
}
