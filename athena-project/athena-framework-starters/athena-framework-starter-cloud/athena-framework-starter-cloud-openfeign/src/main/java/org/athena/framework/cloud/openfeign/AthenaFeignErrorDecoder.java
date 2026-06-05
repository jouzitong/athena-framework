package org.athena.framework.cloud.openfeign;

import feign.Response;
import feign.codec.ErrorDecoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 默认 Feign 错误解码器。
 */
public class AthenaFeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response == null) {
            return new IllegalStateException("Feign response is null, methodKey=" + methodKey);
        }

        String body = null;
        if (response.body() != null) {
            try {
                BufferedReader reader = new BufferedReader(response.body().asReader(StandardCharsets.UTF_8));
                body = reader.readLine();
            } catch (IOException ignored) {
                body = null;
            }
        }

        String message = "Feign call failed, methodKey=" + methodKey + ", status=" + response.status();
        if (body != null && !body.isBlank()) {
            message = message + ", body=" + body;
        }
        return new IllegalStateException(message);
    }
}
