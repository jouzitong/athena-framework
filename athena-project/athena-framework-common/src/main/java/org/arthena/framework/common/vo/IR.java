package org.arthena.framework.common.vo;

import java.io.Serializable;

/**
 * @author zhouzhitong
 * @since 2024/12/10
 **/
public interface IR<T> extends Serializable {

    Integer getCode();

    String getMsg();

    T getData();

}
