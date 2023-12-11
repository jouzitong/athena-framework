package org.arthena.common.base;

import java.io.Serializable;

/**
 * 如果需要将对象存储到数据库中，需要实现该接口
 *
 * @author zhouzhitong
 * @since 2023/5/24
 */
//@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "serialVersionUID")
public interface DBJson extends Serializable {

}
