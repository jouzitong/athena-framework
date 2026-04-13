package org.athena.framework.security.auth.core.web;

import jakarta.servlet.http.HttpServletResponse;
import org.arthena.framework.common.utils.JacksonJsonUtils;

import java.nio.charset.StandardCharsets;

/**
 * 安全模块 HTTP 响应写入工具。
 */
public final class SecurityHttpResponseWriter {

    private SecurityHttpResponseWriter() {
    }

    public static void writeJson(HttpServletResponse response, int httpStatus, int code, Object... args) {
        response.setStatus(httpStatus);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json");
        try {
            String body = JacksonJsonUtils.writeValueAsString(new SecurityErrorResponse(code, args));
            response.getWriter().write(body);
        } catch (Exception ignored) {
            try {
                response.getWriter().write("{\"code\":" + code + ",\"data\":null,\"msg\":\"\"}");
            } catch (Exception ignoredAgain) {
                // ignore
            }
        }
    }
}

