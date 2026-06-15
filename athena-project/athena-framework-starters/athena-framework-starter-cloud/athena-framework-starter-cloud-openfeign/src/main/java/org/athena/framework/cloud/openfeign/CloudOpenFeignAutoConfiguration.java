package org.athena.framework.cloud.openfeign;

import feign.Logger;
import feign.Request;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.arthena.framework.common.constant.RequestHeaderConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import feign.RequestInterceptor;

import java.lang.reflect.Method;

/**
 * OpenFeign starter 自动配置。
 * 默认扫描 org.athena 下的 Feign 客户端接口。
 */
@AutoConfiguration
@Slf4j
@ConditionalOnProperty(prefix = "athena.cloud.openfeign", name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnClass(name = "org.springframework.cloud.openfeign.FeignClientFactoryBean")
@EnableConfigurationProperties(AthenaOpenFeignProperties.class)
@Import(AthenaFeignClientsRegistrar.class)
public class CloudOpenFeignAutoConfiguration {

    public CloudOpenFeignAutoConfiguration() {
        LOGGER.info("Cloud OpenFeign 自动化配置加载中...");
    }

    @Bean
    public Logger.Level feignLoggerLevel(AthenaOpenFeignProperties properties) {
        return properties.getLoggerLevel();
    }

    @Bean
    public Request.Options feignRequestOptions(AthenaOpenFeignProperties properties) {
        return new Request.Options(properties.getConnectTimeoutMillis(), properties.getReadTimeoutMillis());
    }

    @Bean
    public Retryer feignRetryer() {
        return Retryer.NEVER_RETRY;
    }

    @Bean
    public ErrorDecoder feignErrorDecoder() {
        return new AthenaFeignErrorDecoder();
    }

    @Bean
    public RequestInterceptor athenaFeignRequestInterceptor(AthenaOpenFeignProperties properties,
                                                            Environment environment) {
        return template -> {
            String applicationName = environment.getProperty("spring.application.name", "athena");
            template.header(properties.getApplicationNameHeader(), applicationName);

            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes == null) {
                return;
            }

            Object request = resolveCurrentRequest(requestAttributes);
            if (request == null) {
                return;
            }

            copyHeader(request, template, RequestHeaderConstant.TRACE_ID);
            copyHeader(request, template, RequestHeaderConstant.AUTHORIZATION);
            copyHeader(request, template, RequestHeaderConstant.TOKEN);
            copyHeader(request, template, RequestHeaderConstant.USER_ID);
            copyHeader(request, template, RequestHeaderConstant.USER_NAME);
            copyHeader(request, template, RequestHeaderConstant.USER_DISPLAY_NAME);
            copyHeader(request, template, RequestHeaderConstant.TENANT_ID);
            copyHeader(request, template, RequestHeaderConstant.LOCALE);
        };
    }

    private Object resolveCurrentRequest(RequestAttributes requestAttributes) {
        try {
            Method getRequestMethod = requestAttributes.getClass().getMethod("getRequest");
            return getRequestMethod.invoke(requestAttributes);
        } catch (Exception ignored) {
            return null;
        }
    }

    private void copyHeader(Object request, feign.RequestTemplate template, String headerName) {
        try {
            Method getHeaderMethod = request.getClass().getMethod("getHeader", String.class);
            Object headerValue = getHeaderMethod.invoke(request, headerName);
            if (headerValue instanceof String value && StringUtils.isNotBlank(value)) {
                template.header(headerName, value);
            }
        } catch (Exception ignored) {
            // ignore and continue
        }
    }
}
