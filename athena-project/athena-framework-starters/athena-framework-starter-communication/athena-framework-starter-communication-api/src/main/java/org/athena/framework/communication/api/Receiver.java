package org.athena.framework.communication.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通信接收方。
 *
 * @author zhouzhitong
 * @since 2026/6/24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Receiver {

    private ReceiverType type;

    private String target;

    private String name;
}
