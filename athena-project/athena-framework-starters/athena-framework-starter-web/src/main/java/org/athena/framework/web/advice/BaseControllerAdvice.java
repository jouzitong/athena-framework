package org.athena.framework.web.advice;

import com.google.common.collect.Lists;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.arthena.framework.common.constant.CodeConstant;
import org.arthena.framework.common.exception.base.BaseRuntimeException;
import org.arthena.framework.common.exception.base.BaseHttpRuntimeException;
import org.arthena.framework.common.exception.ResourceNotFindException;
import org.arthena.framework.common.utils.JacksonJsonUtils;
import org.athena.framework.web.vo.R;
import org.athena.framework.web.vo.ErrorParamVO;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * 全局默认异常处理
 *
 * @author zhouzhitong
 * @since 2022/05/21
 */
@RestControllerAdvice
@Slf4j
public class BaseControllerAdvice {

    /**
     * 未知异常
     */
    @ExceptionHandler(Exception.class)
    public R<Void> exception(Exception e) {
        LOGGER.error("", e);
        return R.fail(CodeConstant.UN_KNOW_ERROR);
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(BaseRuntimeException.class)
    public R<Void> baseRuntimeException(BaseRuntimeException e, HttpServletResponse response) {
        if (e instanceof BaseHttpRuntimeException httpException) {
            response.setStatus(httpException.getHttpStatus());
            LOGGER.warn("httpStatus={}, code={}", httpException.getHttpStatus(), e.getCode(), e);
        } else {
            LOGGER.error("", e);
        }
        return R.fail(e.getCode(), e.getArgs());
    }

    @ExceptionHandler(ResourceNotFindException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public R<Void> resourceNotFindException(ResourceNotFindException e) {
        LOGGER.warn("", e);
        return R.fail(CodeConstant.RESOURCE_NOT_FOUND, e.getArgs());
    }

    /**
     * 参数校验异常. @Valid 校验抛出的异常拦截
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        LOGGER.warn("methodArgumentNotValid", e);
        // 从异常对象中拿到ObjectError对象
        List<ObjectError> errors = e.getBindingResult().getAllErrors();
        List<ErrorParamVO> res = Lists.newArrayList();
        for (ObjectError error : errors) {
            ErrorParamVO vo = new ErrorParamVO();
            vo.setCode(error.getCode());
            vo.setName(error.getObjectName());
            vo.setMessage(error.getDefaultMessage());
            res.add(vo);
        }
        return R.fail(CodeConstant.ILLEGAL_PARAMETER_ERROR, JacksonJsonUtils.writeValueAsString(res));
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> bindException(BindException e) {
        LOGGER.warn("bindException", e);
        List<ObjectError> errors = e.getBindingResult().getAllErrors();
        List<ErrorParamVO> res = Lists.newArrayList();
        for (ObjectError error : errors) {
            ErrorParamVO vo = new ErrorParamVO();
            vo.setCode(error.getCode());
            vo.setName(error.getObjectName());
            vo.setMessage(error.getDefaultMessage());
            res.add(vo);
        }
        return R.fail(CodeConstant.ILLEGAL_PARAMETER_ERROR, JacksonJsonUtils.writeValueAsString(res));
    }

    @ExceptionHandler({
        MissingServletRequestParameterException.class,
        MethodArgumentTypeMismatchException.class,
        HttpMessageNotReadableException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> badRequest(Exception e) {
        LOGGER.warn("badRequest: {}", e.getClass().getSimpleName(), e);
        return R.fail(CodeConstant.ILLEGAL_PARAMETER_ERROR, e.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public R<Void> methodNotAllowed(HttpRequestMethodNotSupportedException e) {
        LOGGER.warn("methodNotAllowed", e);
        return R.fail(CodeConstant.NOT_SUPPORT_ERROR, e.getMethod());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public R<Void> unsupportedMediaType(HttpMediaTypeNotSupportedException e) {
        LOGGER.warn("unsupportedMediaType", e);
        return R.fail(CodeConstant.ILLEGAL_PARAMETER_ERROR, e.getContentType());
    }

}
