package org.arthena.lib.common.vo;

import org.arthena.lib.common.constant.CodeConstant;
import org.arthena.lib.common.exception.Code;
import lombok.Data;

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
    private final Code code;

    /**
     * 请求出现异常时, 给前端的提示信息
     */
    private final String msg;

    /**
     * 具体数据
     */
    private T data;

    protected ResultVO() {
        this(CodeConstant.SUCCESS);
    }

    protected ResultVO(T data) {
        this(CodeConstant.SUCCESS);
        this.data = data;
    }


    protected ResultVO(Code code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResultVO(Code code) {
        this(code,code.desc());
    }

    public Integer getCode() {
        return code.code();
    }

    public String getDesc() {
        return code.desc();
    }

//    protected ResultVO(Code code, T data) {
//        this.code = code;
//        this.data = data;
//    }

    public static ResultVO<Void> success() {
        return new ResultVO<>();
    }

    public static <T> ResultVO<T> success(T data) {
        return new ResultVO<>(data);
    }

    public static <T> ResultVO<T> ok(T data) {
        return new ResultVO<>(data);
    }

    public static ResultVO<Void> fail(String msg) {
        return new ResultVO<>(CodeConstant.FAIL, msg);
    }

    public static ResultVO<Void> fail(Code code) {
        return new ResultVO<>(code);
    }

    public static ResultVO<Void> fail(Code code, String msg) {
        return new ResultVO<>(code, msg);
    }

//    public static  <T> ResultVO<T> fail(Code code, T data) {
//        return new ResultVO<>(code, data);
//    }

    public static ResultVO<Void> fail() {
        return new ResultVO<>(CodeConstant.FAIL);
    }

}
