package org.athena.framework.communication.api;

import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 统一发送请求。
 *
 * @author zhouzhitong
 * @since 2026/6/24
 */
@Data
public class SendRequest {

    private String bizType;

    private ChannelType channel;

    private String templateCode;

    private List<Receiver> receivers = new ArrayList<>();

    private String title;

    private String content;

    private Map<String, Object> templateParams = new LinkedHashMap<>();

    private Map<String, Object> attributes = new LinkedHashMap<>();
}
