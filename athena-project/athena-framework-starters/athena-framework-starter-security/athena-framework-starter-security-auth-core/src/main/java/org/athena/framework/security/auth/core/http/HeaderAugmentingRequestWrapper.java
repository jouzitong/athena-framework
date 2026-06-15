package org.athena.framework.security.auth.core.http;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 *
 * @author zhouzhitong
 * @since 2026/6/6
 */
public class HeaderAugmentingRequestWrapper extends HttpServletRequestWrapper {

    private final Map<String, List<String>> extraHeaders;

    public HeaderAugmentingRequestWrapper(HttpServletRequest request, Map<String, List<String>> extraHeaders) {
        super(request);
        this.extraHeaders = extraHeaders;

    }

    @Override
    public String getHeader(String name) {
        String value = firstValue(extraHeaders, name);
        if (value != null) {
            return value;
        }
        return super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        List<String> values = new ArrayList<>();
        String extraValue = firstValue(extraHeaders, name);
        if (extraValue != null) {
            values.add(extraValue);
        }
        Enumeration<String> original = super.getHeaders(name);
        while (original.hasMoreElements()) {
            values.add(original.nextElement());
        }
        return Collections.enumeration(values);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        List<String> names = new ArrayList<>();
        Enumeration<String> original = super.getHeaderNames();
        while (original.hasMoreElements()) {
            names.add(original.nextElement());
        }
        for (String name : extraHeaders.keySet()) {
            if (names.stream().noneMatch(existing -> existing.equalsIgnoreCase(name))) {
                names.add(name);
            }
        }
        return Collections.enumeration(names);
    }

    private String firstValue(Map<String, List<String>> headers, String name) {
        if (!StringUtils.hasText(name)) {
            return null;
        }
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(name) && entry.getValue() != null && !entry.getValue().isEmpty()) {
                return entry.getValue().get(0);
            }
        }
        return null;
    }
}