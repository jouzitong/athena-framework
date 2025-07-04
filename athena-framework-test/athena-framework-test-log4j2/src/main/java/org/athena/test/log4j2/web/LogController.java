package org.athena.test.log4j2.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhouzhitong
 * @since 2025/6/12
 **/
@RestController
@RequestMapping("/api/v1/log")
public class LogController {

    private static final Logger log = LoggerFactory.getLogger(LogController.class);

    @GetMapping("/log")
    public String log(){
        log.info("log");

        return "log";
    }

}
