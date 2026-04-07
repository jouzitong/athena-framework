package org.athena.framework.security.auth.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "athena.security.auth")
public class SecurityAuthProperties {

    private boolean enabled = true;

    private String tokenHeader = "Authorization";

    private String tokenPrefix = "Bearer";

    private List<String> ignoreUrls = new ArrayList<>();
}
