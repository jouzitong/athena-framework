package org.arthena.framework.common.utils;

/**
 * @author zhouzhitong
 * @since 2024-01-26
 **/
public class SystemUtils {

    public static String getDir(){
        return System.getProperty("user.dir");
    }

    public static String resolveServiceName() {
        String springAppName = System.getProperty("spring.application.name");
        if (springAppName != null && !springAppName.isBlank()) {
            return springAppName;
        }
        String envAppName = System.getenv("SPRING_APPLICATION_NAME");
        if (envAppName != null && !envAppName.isBlank()) {
            return envAppName;
        }
        return "unknown-service";
    }

}
