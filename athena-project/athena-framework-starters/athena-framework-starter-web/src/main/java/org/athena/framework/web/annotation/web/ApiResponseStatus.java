package org.athena.framework.web.annotation.web;

import org.springframework.core.annotation.AliasFor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zhouzhitong
 * @see ResponseStatus
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ResponseStatus
@Inherited
public @interface ApiResponseStatus {

    /**
     * Alias for {@link #code}.
     */
    @AliasFor(annotation = ResponseStatus.class)
    HttpStatus value() default HttpStatus.INTERNAL_SERVER_ERROR;

    /**
     * The status <em>code</em> to use for the response.
     * <p>Default is {@link HttpStatus#INTERNAL_SERVER_ERROR}, which should
     * typically be changed to something more appropriate.
     *
     * @see jakarta.servlet.http.HttpServletResponse#setStatus(int)
     * @see jakarta.servlet.http.HttpServletResponse#sendError(int)
     * @since 4.2
     */
    @AliasFor(annotation = ResponseStatus.class)
    HttpStatus code() default HttpStatus.INTERNAL_SERVER_ERROR;

    /**
     * The <em>reason</em> to be used for the response.
     * <p>Defaults to an empty string which will be ignored. Set the reason to a
     * non-empty value to have it used to send a Servlet container error page.
     * In this case, the return value of the handler method will be ignored.
     *
     * @see jakarta.servlet.http.HttpServletResponse#sendError(int, String)
     */
    @AliasFor(annotation = ResponseStatus.class)
    String reason() default "";
}
