package org.arthena.framework.common.constant;

/**
 * 所有的错误码
 * <p>
 * 错误码定义:
 * <li>20开头: 业务相关异常</li>
 * <li>40开头: 用户相关异常</li>
 * <li>41开头: 参数相关异常</li>
 * <li>50开头: 程序逻辑相关异常</li>
 *
 * @author zhouzhitong
 * @version 1.0
 * @since 2022/5/19 23:11
 */
public interface CodeConstant {

    Integer SUCCESS = 0;


    String UN_KNOW_ERROR_MSG = "未知异常";

    /**
     * 请求合法的失败
     */
    Integer UN_KNOW_ERROR = 1;

    /**
     * 重复请求
     */
    Integer DUPLICATE_REQUEST = 3;

    Integer RESOURCE_NOT_FOUND = 4;

    Integer CONFIG_ERROR = 5;

    /**
     * 对象存储上传失败
     */
    Integer OBJECT_STORAGE_UPLOAD_ERROR = 31001;

    /**
     * 对象存储下载失败
     */
    Integer OBJECT_STORAGE_DOWNLOAD_ERROR = 31002;

    /**
     * 对象存储删除失败
     */
    Integer OBJECT_STORAGE_DELETE_ERROR = 31003;

    /**
     * 对象存储对象不存在
     */
    Integer OBJECT_STORAGE_NOT_FOUND = 31004;

    /**
     * 对象存储预签名失败
     */
    Integer OBJECT_STORAGE_PRESIGN_ERROR = 31005;

    /**
     * 对象存储 bucket 初始化失败
     */
    Integer OBJECT_STORAGE_BUCKET_INIT_ERROR = 31006;

    /**
     * 对象存储配置非法
     */
    Integer OBJECT_STORAGE_CONFIG_ERROR = 31007;

    Integer ILLEGAL_PARAMETER_ERROR = 10101;

    /**
     * 请求出现异常而导致为捕获异常的失败
     */
    Integer TODO_ERROR = 99999;

    /**
     * 表示操作或请求不被支持的错误代码。
     * 该错误码用于标识系统中当前不支持的功能或请求类型，帮助开发者和维护者快速定位问题。
     * 错误码值为99980。
     */
    Integer NOT_SUPPORT_ERROR = 99980;
}
