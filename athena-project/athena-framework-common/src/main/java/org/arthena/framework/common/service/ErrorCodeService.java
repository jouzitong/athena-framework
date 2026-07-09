package org.arthena.framework.common.service;

/**
 * 错误码扩展服务。
 *
 * @author zhouzhitong
 */
public interface ErrorCodeService {

    /**
     * 返回当前服务的查询顺序，数值越小越先查询。
     *
     * @return 顺序
     */
    default int order() {
        return 0;
    }

    /**
     * 根据错误码和语言返回错误文案。
     *
     * @param code   错误码
     * @param locale 语言
     * @return 错误文案，未命中时返回 null
     */
    String getMsg(Integer code, String locale);

}
