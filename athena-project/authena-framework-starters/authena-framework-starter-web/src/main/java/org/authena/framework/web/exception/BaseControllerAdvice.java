package org.authena.framework.web.exception;

import org.arthena.common.constant.CodeConstant;
import org.arthena.common.exception.BaseException;
import org.arthena.common.exception.ResourceNotFindException;
import org.arthena.common.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
    public ResultVO<Void> unException(Exception e) {
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

//    /**
//     * 参数校验异常
//     */
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResultVO<List<ErrorParamVO>> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
//        LOGGER.error("", e);
//        // 从异常对象中拿到ObjectError对象
//        List<ObjectError> errors = e.getBindingResult().getAllErrors();
//        List<ErrorParamVO> res = Lists.newArrayList();
//        for (ObjectError error : errors) {
//            ErrorParamVO vo = new ErrorParamVO();
//            vo.setCode(error.getCode());
//            vo.setName(error.getObjectName());
//            vo.setMessage(error.getDefaultMessage());
//            res.add(vo);
//        }
//        return ResultVO.fail(CodeConstant.ILLEGAL_PARAMETER_ERROR, res);
//    }

}