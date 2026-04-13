package org.athena.framework.security.auth.core.web;

import lombok.Getter;
import org.arthena.framework.common.utils.ErrorCodeUtils;

/**
 * 安全模块 HTTP 错误响应（最小对齐 R 的 JSON 结构：code/data/msg）。
 * <p>
 * 说明：
 * <ul>
 *     <li>auth-core 不依赖 starter-web 的 R，因此使用独立 DTO。</li>
 *     <li>字段与 R 保持一致，便于调用方统一解析。</li>
 * </ul>
 */
@Getter
public class SecurityErrorResponse {

    private final int code;

    private final Object data = null;

    private final Object[] errorMsgArgs;

    public SecurityErrorResponse(int code, Object... errorMsgArgs) {
        this.code = code;
        this.errorMsgArgs = errorMsgArgs;
    }

    public String getMsg() {
        return ErrorCodeUtils.getMsg(code, errorMsgArgs);
    }
}

