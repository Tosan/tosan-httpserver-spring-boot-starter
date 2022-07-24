package com.tosan.http.server.starter.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.tosan.http.server.starter.util.HttpLogUtil;
import com.tosan.http.server.starter.wrapper.CustomHttpServletRequestWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.DispatcherType;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.Mockito.*;

/**
 * @author M.khoshnevisan
 * @since 6/28/2021
 */
public class HttpLoggingFilterUTest {

    private HttpServletRequest httpServletRequest;
    private HttpServletResponse httpServletResponse;
    private FilterChain filterChain;
    private HttpLoggingFilter loggingFilter;
    private HttpLogUtil httpLogUtil;

    @BeforeEach
    public void setup() {
        httpServletRequest = mock(HttpServletRequest.class);
        httpServletResponse = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);
        httpLogUtil = mock(HttpLogUtil.class);
        loggingFilter = new HttpLoggingFilter(httpLogUtil);
    }

    @Test
    public void filterHttpLog_onAsyncRequests_filterNotApplied() throws ServletException, IOException {
        when(httpServletRequest.getDispatcherType()).thenReturn(DispatcherType.ASYNC);
        loggingFilter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);
        verify(filterChain, times(1)).doFilter(httpServletRequest, httpServletResponse);
    }

    @Test
    public void filterHttpLog_debugMode_callHttpLogUtil() throws ServletException, IOException {
        Logger httpLogUtilLogger = (Logger) LoggerFactory.getLogger(HttpLoggingFilter.class);
        httpLogUtilLogger.setLevel(Level.DEBUG);
        loggingFilter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);
        verify(httpLogUtil, times(1)).logRequest(any(CustomHttpServletRequestWrapper.class));
        verify(httpLogUtil, times(1)).logResponse(any(ContentCachingResponseWrapper.class));
    }

    @Test
    public void filterHttpLog_nonDebugMode_notCallHttpLogUtil() throws ServletException, IOException {
        Logger httpLogUtilLogger = (Logger) LoggerFactory.getLogger(HttpLoggingFilter.class);
        httpLogUtilLogger.setLevel(Level.WARN);
        loggingFilter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);
        verify(httpLogUtil, times(0)).logRequest(any(CustomHttpServletRequestWrapper.class));
        verify(httpLogUtil, times(0)).logResponse(any(ContentCachingResponseWrapper.class));
    }

    @Test
    public void filterHttpLog_OnSyncRequestsWithNonCustomRequestAndResponse_filterApplied() throws ServletException, IOException {
        when(httpServletRequest.getDispatcherType()).thenReturn(DispatcherType.REQUEST);
        loggingFilter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);
        verify(filterChain, times(1)).doFilter(any(CustomHttpServletRequestWrapper.class), any(ContentCachingResponseWrapper.class));
    }

    @Test
    public void filterHttpLog_OnSyncRequestsWithCustomRequestAndResponse_filterApplied() throws ServletException, IOException {
        CustomHttpServletRequestWrapper httpServletRequest = mock(CustomHttpServletRequestWrapper.class);
        ContentCachingResponseWrapper httpServletResponse = mock(ContentCachingResponseWrapper.class);
        when(httpServletRequest.getDispatcherType()).thenReturn(DispatcherType.REQUEST);
        loggingFilter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);
        verify(filterChain, times(1)).doFilter(eq(httpServletRequest), eq(httpServletResponse));
    }
}