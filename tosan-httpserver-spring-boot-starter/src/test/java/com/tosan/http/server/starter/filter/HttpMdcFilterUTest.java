package com.tosan.http.server.starter.filter;

import com.tosan.http.server.starter.util.MdcUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * @author mina khoshnevisan
 * @since 7/17/2022
 */
public class HttpMdcFilterUTest {

    private HttpMdcFilter httpMdcFilter;
    private MdcUtil mdcLogUtil;

    @BeforeEach
    public void setup() {
        mdcLogUtil = mock(MdcUtil.class);
        httpMdcFilter = new HttpMdcFilter(mdcLogUtil);
    }

    @Test
    public void testDoFilterInternal_methodCallWithNoException_createMdcAndClear() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        httpMdcFilter.doFilterInternal(request, response, filterChain);
        verify(mdcLogUtil, times(1)).fillRemoteClientIp(request);
        verify(mdcLogUtil, times(1)).extractHeaderMdcParameters(eq(request));
        verify(filterChain, times(1)).doFilter(eq(request), eq(response));
        verify(mdcLogUtil, times(1)).clear();
    }

    @Test
    public void testDoFilterInternal_exceptionHappenInDoFilter_createMdcAndClear() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        doThrow(new ServletException()).when(filterChain).doFilter(eq(request), eq(response));
        assertThrows(ServletException.class, () -> httpMdcFilter.doFilterInternal(request, response, filterChain));
        verify(mdcLogUtil, times(1)).fillRemoteClientIp(request);
        verify(mdcLogUtil, times(1)).extractHeaderMdcParameters(eq(request));
        verify(filterChain, times(1)).doFilter(eq(request), eq(response));
        verify(mdcLogUtil, times(1)).clear();
    }
}