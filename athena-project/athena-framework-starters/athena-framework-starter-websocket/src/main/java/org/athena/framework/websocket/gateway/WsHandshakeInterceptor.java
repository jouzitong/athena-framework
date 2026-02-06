package org.athena.framework.websocket.gateway;

import java.util.Map;
import org.athena.framework.websocket.metrics.WsMetrics;
import org.athena.framework.websocket.security.TokenInfo;
import org.athena.framework.websocket.security.TokenService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

public class WsHandshakeInterceptor implements HandshakeInterceptor {

    public static final String ATTR_USER_ID = "ws.userId";
    public static final String ATTR_CLAIMS = "ws.claims";
    public static final String ATTR_CLIENT_ID = "ws.clientId";
    public static final String ATTR_RESUME_ID = "ws.resumeId";

    private final TokenService tokenService;
    private final WsMetrics metrics;

    public WsHandshakeInterceptor(TokenService tokenService, WsMetrics metrics) {
        this.tokenService = tokenService;
        this.metrics = metrics;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        // 解析鉴权 Token，失败直接拒绝握手
        String token = extractToken(request.getHeaders(), request.getURI().toString());
        TokenInfo tokenInfo = tokenService.parse(token);
        if (tokenInfo == null) {
            metrics.onHandshakeFailed();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }
        // 将鉴权结果写入连接属性，供后续创建会话使用
        attributes.put(ATTR_USER_ID, tokenInfo.getUserId());
        attributes.put(ATTR_CLAIMS, tokenInfo.getClaims());
        MultiValueMap<String, String> params = UriComponentsBuilder.fromUri(request.getURI()).build().getQueryParams();
        String clientId = params.getFirst("clientId");
        String resumeId = params.getFirst("resumeId");
        if (clientId != null) {
            attributes.put(ATTR_CLIENT_ID, clientId);
        }
        if (resumeId != null) {
            attributes.put(ATTR_RESUME_ID, resumeId);
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
    }

    private String extractToken(HttpHeaders headers, String uri) {
        // 优先 Header Bearer 方式，其次从 Query 参数读取
        String auth = headers.getFirst(HttpHeaders.AUTHORIZATION);
        if (auth != null && auth.startsWith("Bearer ")) {
            return auth.substring("Bearer ".length());
        }
        MultiValueMap<String, String> params = UriComponentsBuilder.fromUriString(uri).build().getQueryParams();
        return params.getFirst("token");
    }
}
