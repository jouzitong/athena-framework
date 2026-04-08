package org.athena.framework.security.starter.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
/**
 * 安全模块总配置。
 * 聚合 token、授权与用户数据源等子模块开关配置。
 */
@ConfigurationProperties(prefix = "athena.security")
public class SecurityProperties {

    private boolean enabled = true;

    private Token token = new Token();

    private Authorization authorization = new Authorization();

    private User user = new User();

    @Data
    /**
     * token 子配置。
     * 定义 token 类型并声明各实现模块是否启用。
     */
    public static class Token {

        private String type = "local";

        private Jwt jwt = new Jwt();

        private Redis redis = new Redis();
    }

    @Data
    /**
     * JWT token 开关配置。
     */
    public static class Jwt {

        private boolean enabled = false;
    }

    @Data
    /**
     * Redis token 开关配置。
     */
    public static class Redis {

        private boolean enabled = false;
    }

    @Data
    /**
     * 授权模块配置。
     */
    public static class Authorization {

        private boolean enabled = false;
    }

    @Data
    /**
     * 用户模块配置。
     */
    public static class User {

        private Jpa jpa = new Jpa();

        @Data
        /**
         * JPA 用户子模块配置。
         */
        public static class Jpa {

            private boolean enabled = false;
        }
    }
}
