package com.tosan.http.server.starter.filter;

import com.tosan.http.server.starter.util.Constants;
import com.tosan.http.server.starter.util.HttpLogUtil;
import com.tosan.http.server.starter.wrapper.CustomHttpServletRequestWrapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Collections;

/**
 * this filter logs http request and response on debug mode.
 * intended fields in request or response or header will be masked before logging
 *
 * @author M.khoshnevisan
 * @since 4/21/2021
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class HttpLoggingFilter extends OncePerRequestFilterBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpLoggingFilter.class);

    private final HttpLogUtil httpLogUtil;

    public HttpLoggingFilter(HttpLogUtil httpLogUtil) {
        this.httpLogUtil = httpLogUtil;
        super.addExcludeUrlPatterns(Collections.singletonList(Constants.DEFAULT_ACTUATOR_EXCLUDE_PATTERN));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (isAsyncDispatch(httpServletRequest)) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } else {
            doFilterWrapped(wrapRequest(httpServletRequest), wrapResponse(httpServletResponse), filterChain);
        }
    }

    private void doFilterWrapped(CustomHttpServletRequestWrapper request, ContentCachingResponseWrapper response,
                                 FilterChain filterChain) throws ServletException, IOException {
        try {
            if (LOGGER.isDebugEnabled()) {
                httpLogUtil.logRequest(request);
            }
            filterChain.doFilter(request, response);
        } finally {
            if (LOGGER.isDebugEnabled()) {
                httpLogUtil.logResponse(response);
            }
            response.copyBodyToResponse();
        }
    }

    private CustomHttpServletRequestWrapper wrapRequest(HttpServletRequest request) {
        if (request instanceof CustomHttpServletRequestWrapper) {
            return (CustomHttpServletRequestWrapper) request;
        } else {
            return new CustomHttpServletRequestWrapper(request);
        }
    }

    private ContentCachingResponseWrapper wrapResponse(HttpServletResponse response) {
        if (response instanceof ContentCachingResponseWrapper) {
            return (ContentCachingResponseWrapper) response;
        } else {
            return new ContentCachingResponseWrapper(response);
        }
    }
}