package org.arthena.framework.common.constant;

/**
 * 所有的错误码
 * <p>
 * 错误码定义:
 * <li>20开头: 业务相关异常</li>
 * <li>40开头: 用户相关异常</li>
 * <li>41开头: 参数相关异常</li>
 * <li>50开头: 程序逻辑相关异常</li>
 *
 * @author zhouzhitong
 * @version 1.0
 * @since 2022/5/19 23:11
 */
public interface CodeConstant {

    Integer SUCCESS = 0;


    String UN_KNOW_ERROR_MSG = "未知异常";

    /**
     * 请求合法的失败
     */
    Integer UN_KNOW_ERROR = 1;

    /**
     * 重复请求
     */
    Integer DUPLICATE_REQUEST = 3;

    Integer RESOURCE_NOT_FOUND = 4;

    Integer CONFIG_ERROR = 5;


    Integer ILLEGAL_PARAMETER_ERROR = 10101;

    /**
     * 请求出现异常而导致为捕获异常的失败
     */
    Integer TODO_ERROR = 99999;
}
