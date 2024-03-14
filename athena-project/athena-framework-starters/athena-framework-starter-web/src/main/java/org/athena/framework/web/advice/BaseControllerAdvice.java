package org.athena.framework.web.advice;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.arthena.framework.common.constant.CodeConstant;
import org.arthena.framework.common.exception.BaseException;
import org.arthena.framework.common.exception.ResourceNotFindException;
import org.arthena.framework.common.utils.JacksonJsonUtils;
import org.arthena.framework.common.vo.ResultVO;
import org.athena.framework.web.vo.ErrorParamVO;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
    public ResultVO<Void> exception(Exception e) {
        LOGGER.error("", e);
        return ResultVO.fail(CodeConstant.UN_KNOW_ERROR, e.getMessage());
    }

    /**
     * 未知运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ResultVO<Void> runtimeException(RuntimeException e) {
        LOGGER.error("", e);
        return ResultVO.fail(CodeConstant.UN_KNOW_ERROR, e.getMessage());
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(BaseException.class)
    public ResultVO<Void> userException(BaseException e) {
        LOGGER.error("", e);
        return ResultVO.fail(e.getCode(), e.getMsg());
    }

    @ExceptionHandler(ResourceNotFindException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResultVO<Void> resourceNotFindException(ResourceNotFindException e) {
        LOGGER.error("", e);
        return ResultVO.fail(CodeConstant.RESOURCE_NOT_FOUND, e.getMessage());
    }

    /**
     * 参数校验异常. @Valid 校验抛出的异常拦截
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultVO<Void> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        LOGGER.error("", e);
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
        return ResultVO.fail(CodeConstant.ILLEGAL_PARAMETER_ERROR, JacksonJsonUtils.writeValueAsString(res));
    }

}