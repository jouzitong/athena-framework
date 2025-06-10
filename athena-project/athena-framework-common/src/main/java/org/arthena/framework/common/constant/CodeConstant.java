package org.arthena.framework.common.constant;

/**
 * 所有的错误码
 *
 * @author zhouzhitong
 * @version 1.0
 * @since 2022/5/19 23:11
 */
public interface CodeConstant {

    Integer SUCCESS = 0;

    /**
     * 请求合法的失败
     */
    Integer FAIL = 1;

    String UN_KNOW_ERROR_MSG = "未知异常";

    Integer UN_KNOW_ERROR = 2;
    Integer DUPLICATE_REQUEST = 3;

    Integer RESOURCE_NOT_FOUND = 4;

    Integer CONFIG_ERROR = 5;

    /**
     * 请求出现异常而导致为捕获异常的失败
     */
    Integer TODO_ERROR = 99999;

    Integer ILLEGAL_PARAMETER_ERROR = 10101;

    Integer USER_LOGIN_ERROR = 40402;

}
