package org.arthena.common.constant;

import org.arthena.common.exception.Code;

/**
 * 所有的错误码
 *
 * @author zhouzhitong
 * @version 1.0
 * @since 2022/5/19 23:11
 */
public interface CodeConstant {

    Code SUCCESS = new Code(0, "成功");

    /**
     * 请求合法的失败
     */
    Code FAIL = new Code(1, "失败");

    Code UN_KNOW_ERROR = new Code(2, "未知异常");

    Code DUPLICATE_REQUEST = new Code(3, "重复请求, 不可重复提交, 请稍后重试");

    Code RESOURCE_NOT_FOUND = new Code(4, "资源不存在");

    Code CONFIG_ERROR = new Code(99998, "配置文件异常");

    /**
     * 请求出现异常而导致为捕获异常的失败
     */
    Code TODO_ERROR = new Code(99999, "功能未开发");

    Code ILLEGAL_PARAMETER_ERROR = new Code(10101, "非法参数异常");

    Code I18N_RESOLVER_ERROR = new Code(30100, "国际化解析异常");

    Code USER_UN_LOGIN = new Code(40400, "未登陆, 请先登陆");

    Code USER_TOKEN_INVALID_ERROR = new Code(40401, "token失效, 请重新登录");

    Code USER_LOGIN_ERROR = new Code(40402, "登录失败, 请重新登录");

    Code PERMISSION_DENIED = new Code(40403, "没有权限");

    Code USERNAME_NOT_FOUND_ERROR = new Code(40404, "用户名不存在");

    Code USERNAME_PASSWORD_NOT_FOUND_ERROR = new Code(40405, "用户名和密码不匹配异常");

    Code USER_DISABLE_ERROR = new Code(40411, "账户被冻结");

    Code USER_ROLE_DISABLE_ERROR = new Code(40412, "角色被冻结");


    Code USER_TOKEN_DENIED_PERMISSION = new Code(40415, "权限不足, 不能访问");

    Code DATABASE_UNKNOWN_EXCEPTION = new Code(50_000, "数据库未知异常");

}
