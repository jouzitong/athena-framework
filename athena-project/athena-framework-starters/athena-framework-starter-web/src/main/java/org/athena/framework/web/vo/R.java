package org.athena.framework.web.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.arthena.framework.common.constant.ErrCodeConstant;
import org.arthena.framework.common.utils.ErrorCodeUtils;
import org.slf4j.MDC;
import org.athena.framework.web.filter.TraceIdFilter;

/**
 * @author zhouzhitong
 * @since 2025/7/6
 **/
@Data
@NoArgsConstructor
public class R<D> implements IR<D> {

    /**
     * 状态码. 除了0 表示成功，其他值表示失败.
     */
    private int code;

    /**
     * 如果 code = 0, data 数据才有效
     */
    private D data;

    /**
     * 响应时间戳（毫秒）
     */
    private Long timestamp;

    /**
     * 链路追踪ID
     */
    private String traceId;

    /**
     * 响应签名
     */
    private String sign;

    /**
     * 签名密钥ID
     */
    private String signKeyId;

    private String msg;

    /**
     * 错误信息参数
     */
    @JsonIgnore
    private Object[] errorMsgArgs;

    protected R(D data) {
        this.code = ErrCodeConstant.SUCCESS;
        this.data = data;
        initMeta();
    }

    protected R(int code, Object... errorMsgArgs) {
        this.code = code;
        this.errorMsgArgs = errorMsgArgs;
        this.data = null;
        initMeta();
    }

    private void initMeta() {
        this.timestamp = System.currentTimeMillis();
        this.traceId = MDC.get(TraceIdFilter.MDC_KEY);
    }

    public String getMsg() {
        return msg = ErrorCodeUtils.getMsg(code, errorMsgArgs);
    }

    @JsonIgnore
    public boolean isOk() {
        return code == ErrCodeConstant.SUCCESS;
    }

    @JsonIgnore
    public boolean isSuccess() {
        return isOk();
    }

    @JsonIgnore
    public boolean isFail() {
        return !isOk();
    }

    public static R<Void> ok() {
        return new R<Void>(ErrCodeConstant.SUCCESS);
    }

    public static <D> R<D> ok(D data) {
        return new R<D>(data);
    }

    public static R<Void> fail(int code, Object... errorMsgArgs) {
        return new R<Void>(code, errorMsgArgs);
    }

}
