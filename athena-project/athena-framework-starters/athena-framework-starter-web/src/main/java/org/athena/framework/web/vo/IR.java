package org.athena.framework.web.vo;

import java.io.Serializable;

/**
 * @author zhouzhitong
 * @since 2024/12/10
 **/
public interface IR<T> extends Serializable {
    /**
     * 返回状态码, 0表示成功, 其他表示失败
     *
     * @return 状态码
     */
    int getCode();

    /**
     * 返回操作对象. 主要是于前端定义好操作规范
     *
     * @return 操作码
     */
    default Object getOpObj() {
        return null;
    }

    /**
     * 描述信息. 如果有操作码, 则是对操作码的描述. 如果操作码没有值, 则描述状态的的信息
     *
     * @return 描述信息
     */
    String getMsg();

    /**
     * 返回数据
     *
     * @return 数据
     */
    T getData();

    /**
     * 获取签名
     *
     * @return 签名
     */
    default String getSign() {
        return null;
    }

}
