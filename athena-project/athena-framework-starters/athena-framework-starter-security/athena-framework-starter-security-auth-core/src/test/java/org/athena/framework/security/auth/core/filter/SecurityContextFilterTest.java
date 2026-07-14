package org.athena.framework.security.auth.core.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.arthena.framework.common.constant.ErrCodeConstant;
import org.arthena.framework.common.exception.BizException;
import org.athena.framework.security.api.spi.TokenManager;
import org.athena.framework.security.auth.core.config.SecurityAuthProperties;
import org.athena.framework.security.auth.core.extractor.CredentialExtractor;
import org.athena.framework.security.auth.core.gateway.GatewayRequestHeaderValidator;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.client.HttpServerErrorException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SecurityContextFilterTest {

    private SecurityContextFilter filter;

    @Before
    public void setUp() {
        CredentialExtractor credentialExtractor = mock(CredentialExtractor.class);
        when(credentialExtractor.extractToken(org.mockito.ArgumentMatchers.any())).thenReturn(null);
        filter = new SecurityContextFilter(
                credentialExtractor,
                mock(TokenManager.class),
                List.of(),
                new SecurityAuthProperties(),
                List.of(),
                mock(GatewayRequestHeaderValidator.class)
        );
    }

    @Test
    public void shouldReturnServiceUnavailableWhenNoServiceInstanceCanBeFound() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/dbEngine/api/v1/query.list");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = (servletRequest, servletResponse) -> {
            HttpServerErrorException unavailable = new HttpServerErrorException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Unable to find instance for dbEngine"
            );
            throw new ServletException("Request processing failed", unavailable);
        };

        filter.doFilterInternal(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE.value());
        assertThat(response.getContentType()).startsWith("application/json");
        assertThat(response.getContentAsString())
                .contains("\"code\":" + ErrCodeConstant.SERVICE_UNAVAILABLE)
                .contains("dbEngine");
    }

    @Test
    public void shouldPreserveBusinessExceptionStatusAndCode() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/user/api/v1/users");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = (servletRequest, servletResponse) -> {
            throw BizException.ofStatus(ErrCodeConstant.ILLEGAL_PARAMETER_ERROR, 422, "username");
        };

        filter.doFilterInternal(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(422);
        assertThat(response.getContentAsString())
                .contains("\"code\":" + ErrCodeConstant.ILLEGAL_PARAMETER_ERROR)
                .contains("username");
    }

    @Test
    public void shouldReturnRootCauseInformationForUnexpectedException() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/test");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = (servletRequest, servletResponse) -> {
            throw new ServletException("Request processing failed", new IllegalStateException("boom"));
        };

        filter.doFilterInternal(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(response.getContentAsString())
                .contains("\"code\":" + ErrCodeConstant.REQUEST_PROCESSING_ERROR)
                .contains("boom");
    }

    @Test
    public void shouldPreserveStatusAndDetailForOtherHttpException() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/upstream");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = (servletRequest, servletResponse) -> {
            throw new ServletException(
                    "Request processing failed",
                    new HttpServerErrorException(HttpStatus.BAD_GATEWAY, "upstream exploded")
            );
        };

        filter.doFilterInternal(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_GATEWAY.value());
        assertThat(response.getContentAsString())
                .contains("\"code\":" + ErrCodeConstant.REQUEST_PROCESSING_ERROR)
                .contains("upstream exploded");
    }

    @Test
    public void shouldNotOverwriteCommittedResponse() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/test");
        MockHttpServletResponse response = new MockHttpServletResponse();
        response.setStatus(HttpStatus.ACCEPTED.value());
        response.setCommitted(true);
        FilterChain chain = (servletRequest, servletResponse) -> {
            throw new IllegalStateException("too late");
        };

        filter.doFilterInternal(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.ACCEPTED.value());
        assertThat(response.getContentAsString()).isEmpty();
    }
}
