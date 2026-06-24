package org.athena.framework.communication.constant;

import org.arthena.framework.common.constant.ErrCodeConstant;

/**
 * 通信错误码别名。
 *
 * @author zhouzhitong
 * @since 2026/6/24
 */
public interface CommunicationErrorCode {

    Integer COMMUNICATION_CONFIG_ERROR = ErrCodeConstant.COMMUNICATION_CONFIG_ERROR;

    Integer COMMUNICATION_CHANNEL_NOT_FOUND = ErrCodeConstant.COMMUNICATION_CHANNEL_NOT_FOUND;

    Integer COMMUNICATION_SEND_ERROR = ErrCodeConstant.COMMUNICATION_SEND_ERROR;

    Integer COMMUNICATION_INVALID_REQUEST = ErrCodeConstant.COMMUNICATION_INVALID_REQUEST;
}
