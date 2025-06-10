package org.arthena.framework.common.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.arthena.framework.common.constant.CodeConstant;

import java.io.Serial;
import java.io.Serializable;

/**
 * 数据返回
 *
 * @author zhouzhitong
 * @version 1.0
 * @since 2022/5/19 23:20
 */
@Data
public class ResultVO<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * code=0 成功, 其他都是失败
     */
    @Getter
    private final Integer code;

    @Setter
    private Object[] args;

    /**
     * 具体数据
     */
    @Getter
    @Setter
    private T data;

    protected ResultVO() {
        this(CodeConstant.SUCCESS);
    }

    protected ResultVO(T data) {
        this(CodeConstant.SUCCESS);
        this.data = data;
    }

    protected ResultVO(Integer code) {
        this.code = code;
    }

    protected ResultVO(Integer code, Object... args) {
        this.code = code;
        this.args = args;
    }

    public String getMsg() {
        // TODO
        return null;
    }

    public static ResultVO<Void> success() {
        return new ResultVO<>();
    }

    public static <T> ResultVO<T> success(T data) {
        return new ResultVO<>(data);
    }

    public static <T> ResultVO<T> ok(T data) {
        return new ResultVO<>(data);
    }

    public static ResultVO<Void> fail(Integer code, Object... args) {
        return new ResultVO<>(code,args);
    }

    public static ResultVO<Void> fail() {
        return new ResultVO<>(CodeConstant.FAIL);
    }

}
