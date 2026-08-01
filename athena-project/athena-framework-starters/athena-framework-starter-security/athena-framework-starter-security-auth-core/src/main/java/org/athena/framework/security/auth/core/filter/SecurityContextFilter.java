package org.athena.framework.security.auth.core.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.arthena.framework.common.constant.ErrCodeConstant;
import org.arthena.framework.common.context.SystemContext;
import org.arthena.framework.common.exception.BizException;
import org.arthena.framework.common.utils.ErrorCodeUtils;
import org.athena.framework.security.api.model.MutableUserContext;
import org.athena.framework.security.api.model.TokenContext;
import org.athena.framework.security.api.model.UserContext;
import org.athena.framework.security.api.spi.SecurityAuthAttributes;
import org.athena.framework.security.api.spi.TokenManager;
import org.athena.framework.security.api.spi.TokenManagerWithParseResult;
import org.athena.framework.security.api.spi.TokenParseResult;
import org.athena.framework.security.api.spi.TokenParseStatus;
import org.athena.framework.security.api.spi.UserContextEnricher;
import org.athena.framework.security.auth.core.config.SecurityAuthProperties;
import org.athena.framework.security.auth.core.extractor.CredentialExtractor;
import org.athena.framework.security.auth.core.gateway.GatewayRequestHeaderValidator;
import org.athena.framework.security.auth.core.web.SecurityHttpResponseWriter;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

/**
 * 安全上下文过滤器。
 * 从请求中提取 token 并解析为 {@link UserContext}，随后绑定到线程上下文供后续链路使用。
 */
@Slf4j
public class SecurityContextFilter extends OncePerRequestFilter {

    private static final String INSTANCE_NOT_FOUND_PREFIX = "Unable to find instance for ";

    private static final String LOAD_BALANCER_NOT_FOUND_PREFIX = "No loadbalancer available for ";

    private final CredentialExtractor credentialExtractor;

    private final TokenManager tokenManager;

    private final List<UserContextEnricher> enrichers;

    private final SecurityAuthProperties properties;

    private final List<SecurityRequestInterceptor> requestInterceptors;

    private final GatewayRequestHeaderValidator gatewayRequestHeaderValidator;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public SecurityContextFilter(CredentialExtractor credentialExtractor,
                                 TokenManager tokenManager,
                                 List<UserContextEnricher> enrichers,
                                 SecurityAuthProperties properties,
                                 List<SecurityRequestInterceptor> requestInterceptors,
                                 GatewayRequestHeaderValidator gatewayRequestHeaderValidator) {
        this.credentialExtractor = credentialExtractor;
        this.tokenManager = tokenManager;
        this.enrichers = enrichers.stream().sorted(Comparator.comparingInt(UserContextEnricher::order)).toList();
        this.properties = properties;
        this.requestInterceptors = requestInterceptors.stream().sorted(Comparator.comparingInt(SecurityRequestInterceptor::order)).toList();
        this.gatewayRequestHeaderValidator = gatewayRequestHeaderValidator;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            String uri = request.getRequestURI();
            LOGGER.debug("Processing request, uri={}", uri);
            boolean ignored = isIgnored(request.getRequestURI());
//            String token = ignored ? null : credentialExtractor.extractToken(request);
            String token = credentialExtractor.extractToken(request);
//            LOGGER.debug("Token extracted, token={}", token);
            UserContext userContext = null;
            TokenParseStatus tokenParseStatus = TokenParseStatus.EMPTY;
//            if (StringUtils.isBlank(token)) {
//                GatewayRequestHeaderValidator.ValidationResult validationResult = parseUserInfo(request);
//                if (validationResult != null && validationResult.valid()) {
//                    userContext = validationResult.userContext();
//                    tokenParseStatus = TokenParseStatus.OK;
//                    LOGGER.debug("Security context restored from gateway headers, uri={}", request.getRequestURI());
//                } else if (validationResult != null) {
//                    tokenParseStatus = TokenParseStatus.INVALID_SIGNATURE;
//                    LOGGER.warn("Gateway user headers rejected, uri={}, reason={}",
//                            request.getRequestURI(), validationResult.reason());
//                }
//            }
            if (StringUtils.isNotBlank(token)) {
                if (tokenManager instanceof TokenManagerWithParseResult tokenManagerWithParseResult) {
                    TokenParseResult tokenParseResult = tokenManagerWithParseResult.parseWithResult(token);
                    userContext = tokenParseResult == null ? null : tokenParseResult.getUserContext();
                    tokenParseStatus = tokenParseResult == null ? TokenParseStatus.ERROR : tokenParseResult.getStatus();
                } else {
                    userContext = tokenManager.parse(token);
                    TokenContext tokenContext = tokenManager.parseV2(token);
                    tokenParseStatus = tokenContext.status();
                }
                if (userContext != null) {
                    if (userContext instanceof MutableUserContext mutableUserContext) {
                        mutableUserContext.setToken(token);
                        for (UserContextEnricher enricher : enrichers) {
                            enricher.enrich(mutableUserContext);
                        }
                    }
                    LOGGER.debug("Security context set user id = {}",
                            userContext.subject().username());
                    SystemContext.setUserContext(userContext);
                    if (userContext.subject() != null) {
                        SystemContext.setTenantId(userContext.subject().tenantId());
                    }
                } else {
                    LOGGER.debug("Token parsed to empty context, uri={}", request.getRequestURI());
                }
            }
            if (!ignored) {
                LOGGER.trace("Security filter active for uri={}", request.getRequestURI());
            }
            request.setAttribute(SecurityAuthAttributes.TOKEN_PARSE_STATUS, tokenParseStatus);
            for (SecurityRequestInterceptor requestInterceptor : requestInterceptors) {
                if (!requestInterceptor.preHandle(request, response, token, userContext, ignored)) {
                    LOGGER.debug("Request blocked by interceptor={}, uri={}",
                            requestInterceptor.getClass().getSimpleName(), request.getRequestURI());
                    return;
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            handleException(request, response, e);
        } finally {
            SystemContext.clearUserContext();
            SystemContext.clearTenantId();
        }
    }

    private void handleException(HttpServletRequest request,
                                 HttpServletResponse response,
                                 Exception exception) throws IOException {
        if (response.isCommitted()) {
            LOGGER.error("Request failed after response committed, uri={}", request.getRequestURI(), exception);
            return;
        }

        ExceptionResponse exceptionResponse = resolveException(exception);
        if (exceptionResponse.serviceUnavailable()) {
            LOGGER.warn("Service unavailable, uri={}, service={}",
                    request.getRequestURI(), exceptionResponse.args()[0], exception);
        } else if (exceptionResponse.businessException()) {
            LOGGER.warn("Business exception, uri={}, status={}, code={}",
                    request.getRequestURI(), exceptionResponse.httpStatus(), exceptionResponse.code(), exception);
        } else {
            LOGGER.error("Request processing failed, uri={}, status={}, detail={}",
                    request.getRequestURI(), exceptionResponse.httpStatus(), exceptionResponse.args()[0], exception);
        }

        response.resetBuffer();
        if (properties.isJsonErrorResponse()) {
            SecurityHttpResponseWriter.writeJson(
                    response,
                    exceptionResponse.httpStatus(),
                    exceptionResponse.code(),
                    exceptionResponse.args()
            );
            return;
        }
        response.sendError(
                exceptionResponse.httpStatus(),
                ErrorCodeUtils.getMsg(exceptionResponse.code(), exceptionResponse.args())
        );
    }

    private ExceptionResponse resolveException(Exception exception) {
        BizException bizException = findCause(exception, BizException.class);
        if (bizException != null) {
            return new ExceptionResponse(
                    normalizeHttpStatus(bizException.getStatus()),
                    bizException.getCode(),
                    bizException.getArgs(),
                    false,
                    true
            );
        }

        HttpStatusCodeException httpException = findCause(exception, HttpStatusCodeException.class);
        if (httpException != null) {
            int httpStatus = httpException.getStatusCode().value();
            String unavailableService = resolveUnavailableService(httpException);
            if (httpStatus == HttpServletResponse.SC_SERVICE_UNAVAILABLE && unavailableService != null) {
                return new ExceptionResponse(
                        HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                        ErrCodeConstant.SERVICE_UNAVAILABLE,
                        new Object[]{unavailableService},
                        true,
                        false
                );
            }
            return new ExceptionResponse(
                    normalizeHttpStatus(httpStatus),
                    ErrCodeConstant.REQUEST_PROCESSING_ERROR,
                    new Object[]{resolveHttpExceptionDetail(httpException)},
                    false,
                    false
            );
        }

        Throwable rootCause = rootCause(exception);
        return new ExceptionResponse(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                ErrCodeConstant.REQUEST_PROCESSING_ERROR,
                new Object[]{resolveThrowableDetail(rootCause)},
                false,
                false
        );
    }

    private String resolveUnavailableService(HttpStatusCodeException exception) {
        String message = exception.getMessage();
        String serviceName = substringAfter(message, INSTANCE_NOT_FOUND_PREFIX);
        if (serviceName == null) {
            serviceName = substringAfter(message, LOAD_BALANCER_NOT_FOUND_PREFIX);
        }
        return serviceName;
    }

    private String substringAfter(String message, String prefix) {
        if (StringUtils.isBlank(message)) {
            return null;
        }
        int prefixIndex = message.indexOf(prefix);
        if (prefixIndex < 0) {
            return null;
        }
        String value = message.substring(prefixIndex + prefix.length()).trim();
        return StringUtils.isBlank(value) ? null : value;
    }

    private String resolveHttpExceptionDetail(HttpStatusCodeException exception) {
        String responseBody = exception.getResponseBodyAsString();
        if (StringUtils.isNotBlank(responseBody)) {
            return responseBody;
        }
        return resolveThrowableDetail(exception);
    }

    private String resolveThrowableDetail(Throwable throwable) {
        if (throwable == null) {
            return ErrCodeConstant.UN_KNOW_ERROR_MSG;
        }
        return StringUtils.defaultIfBlank(throwable.getMessage(), throwable.getClass().getSimpleName());
    }

    private Throwable rootCause(Throwable throwable) {
        Throwable result = throwable;
        while (result.getCause() != null && result.getCause() != result) {
            result = result.getCause();
        }
        return result;
    }

    private <T extends Throwable> T findCause(Throwable throwable, Class<T> causeType) {
        Throwable current = throwable;
        while (current != null) {
            if (causeType.isInstance(current)) {
                return causeType.cast(current);
            }
            if (current.getCause() == current) {
                break;
            }
            current = current.getCause();
        }
        return null;
    }

    private int normalizeHttpStatus(int status) {
        return status >= 100 && status <= 599 ? status : HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
    }

    private record ExceptionResponse(int httpStatus,
                                     int code,
                                     Object[] args,
                                     boolean serviceUnavailable,
                                     boolean businessException) {
    }

    private GatewayRequestHeaderValidator.ValidationResult parseUserInfo(HttpServletRequest request) {
        return gatewayRequestHeaderValidator.validate(request);
    }

    private boolean isIgnored(String requestUri) {
        if (!properties.isEnabled()) {
            return true;
        }
        if (properties.getIgnoreUrls() == null || properties.getIgnoreUrls().isEmpty()) {
            return false;
        }
        return properties.getIgnoreUrls().stream().anyMatch(pattern -> antPathMatcher.match(pattern, requestUri));
    }
}
