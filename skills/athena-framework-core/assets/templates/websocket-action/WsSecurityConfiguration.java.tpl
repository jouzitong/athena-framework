package {{PACKAGE}}.websocket;

import org.athena.framework.websocket.security.AclService;
import org.athena.framework.websocket.security.TokenInfo;
import org.athena.framework.websocket.security.TokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class {{NAME}}WsSecurityConfiguration {

    @Bean
    TokenService {{NAME_LOWER}}WsTokenService(TokenVerifier tokenVerifier) {
        return tokenVerifier::verify;
    }

    @Bean
    AclService {{NAME_LOWER}}WsAclService(WsAuthorizer authorizer) {
        return new AclService() {
            @Override
            public boolean canSubscribe(TokenInfo user, String topic) {
                return authorizer.canSubscribe(user, topic);
            }

            @Override
            public boolean canPublish(TokenInfo user, String topic) {
                return authorizer.canPublish(user, topic);
            }

            @Override
            public boolean canRequest(TokenInfo user, String action) {
                return authorizer.canRequest(user, action);
            }
        };
    }

    @FunctionalInterface
    public interface TokenVerifier {
        TokenInfo verify(String token);
    }

    public interface WsAuthorizer {
        boolean canSubscribe(TokenInfo user, String topic);

        boolean canPublish(TokenInfo user, String topic);

        boolean canRequest(TokenInfo user, String action);
    }
}
