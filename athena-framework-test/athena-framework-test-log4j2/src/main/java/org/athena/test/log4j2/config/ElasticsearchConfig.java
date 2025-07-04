package org.athena.test.log4j2.config;

import org.springframework.beans.factory.annotation.Value;

/**
 * @author zhouzhitong
 * @since 2025/6/12
 **/
public class ElasticsearchConfig {

    @Value("${elasticsearch.host:localhost}")
    private String host;

    @Value("${elasticsearch.port:9200}")
    private int port;

    @Value("${elasticsearch.scheme:http}")
    private String scheme;


}
