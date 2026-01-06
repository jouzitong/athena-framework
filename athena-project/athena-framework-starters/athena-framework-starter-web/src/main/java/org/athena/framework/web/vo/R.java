package org.athena.framework.web.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.arthena.framework.common.constant.CodeConstant;
import org.arthena.framework.common.utils.ErrorCodeUtils;

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
     * 错误信息参数
     */
    private Object[] errorMsgArgs;

    protected R(D data) {
        this.code = CodeConstant.SUCCESS;
        this.data = data;
    }

    protected R(int code, Object... errorMsgArgs) {
        this.code = code;
        this.errorMsgArgs = errorMsgArgs;
        this.data = null;
    }

    public static R<Void> ok() {
        return new R<Void>(CodeConstant.SUCCESS);
    }

    public static <D> R<D> ok(D data) {
        return new R<D>(data);
    }

    public static R<Void> fail(int code, Object... errorMsgArgs) {
        return new R<Void>(code, errorMsgArgs);
    }

    public String getMsg() {
        return ErrorCodeUtils.getMsg(code, errorMsgArgs);
    }

    @JsonIgnore
    public boolean isOk() {
        return code == CodeConstant.SUCCESS;
    }

    @JsonIgnore
    public boolean isSuccess() {
        return isOk();
    }

    @JsonIgnore
    public boolean isFail() {
        return !isOk();
    }

}