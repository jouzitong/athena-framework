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
     * 描述信息，用于说明当前响应状态
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

    /**
     * 获取状态码
     *
     * @return 状态码
     */
    default int getStatus() {
        return getCode() == 0 ? 200 : 500;
    }

    default boolean isOk() {
        return getCode() == 0;
    }


}
