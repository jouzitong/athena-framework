package org.athena.framework.security.starter.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "athena.security")
public class SecurityProperties {

    private boolean enabled = true;

    private Token token = new Token();

    private Authorization authorization = new Authorization();

    private User user = new User();

    @Data
    public static class Token {

        private String type = "local";

        private Jwt jwt = new Jwt();

        private Redis redis = new Redis();
    }

    @Data
    public static class Jwt {

        private boolean enabled = false;
    }

    @Data
    public static class Redis {

        private boolean enabled = false;
    }

    @Data
    public static class Authorization {

        private boolean enabled = false;
    }

    @Data
    public static class User {

        private Jpa jpa = new Jpa();

        @Data
        public static class Jpa {

            private boolean enabled = false;
        }
    }
}
