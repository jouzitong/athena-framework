package org.arthena.framework.common.exception;

import java.io.Serializable;

/**
 * <ol>
 *     <heead>基础异常</heead>
 *     <li>0: 成功</li>
 *     <li>1: 失败</li>
 *     <li>2: 未知异常</li>
 *     <li>3: 重复请求, 不可重复提交, 请稍后重试</li>
 *     <li>4: 非法参数异常</li>
 *     <li>99998: 配置文件异常 (服务启动时的异常)</li>
 *     <li>99999: 功能未开发异常</li>
 * </ol>
 *
 * <ol>
 *     <heead>业务异常</heead>
 *     <li>30xxx: 国际化类异常</li>
 *     <li>40xxx: 账户类异常</li>
 *     <li>50xxx: 数据库类异常</li>
 *     <li>60xxx: 文件处理类异常</li>
 * </ol>
 *
 * @author zhouzhitong
 * @version 1.0
 * @since 2022/5/19 23:11
 */
@Deprecated
public record Code(int code, String desc) implements Serializable {

}