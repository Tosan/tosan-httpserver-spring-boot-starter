package com.tosan.http.server.starter.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

/**
 * @author mina khoshnevisan
 * @since 7/17/2022
 */
public class OncePerRequestFilterBaseUTest {

    private OncePerRequestFilterBase oncePerRequestFilterBase = new HttpLoggingFilter(null);
    private AntPathMatcher antPathMatcher;

    @BeforeEach
    public void setup() {
        antPathMatcher = mock(AntPathMatcher.class);
        oncePerRequestFilterBase.setAntPathMatcher(antPathMatcher);
    }

    @Test
    public void testOncePerRequestFilterBase_noExcludeFilters_returnMatchFalse() {
        oncePerRequestFilterBase.setExcludeUrlPatterns(null);
        HttpServletRequest request = mock(HttpServletRequest.class);
        boolean shouldNotFilter = oncePerRequestFilterBase.shouldNotFilter(request);
        verify(antPathMatcher, times(0)).match(any(), any());
        assertFalse(shouldNotFilter);
    }

    @Test
    public void testOncePerRequestFilterBase_haveExcludeFilters_returnMatchTrue() {
        List<String> excludeUrlPatterns = new ArrayList<>();
        String excludeUrl1 = "/test1";
        excludeUrlPatterns.add(excludeUrl1);
        String excludeUrl2 = "/test2";
        excludeUrlPatterns.add(excludeUrl2);
        oncePerRequestFilterBase.setExcludeUrlPatterns(excludeUrlPatterns);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getServletPath()).thenReturn(excludeUrl2);
        when(antPathMatcher.match(eq(excludeUrl1), eq(excludeUrl2))).thenReturn(false);
        when(antPathMatcher.match(eq(excludeUrl2), eq(excludeUrl2))).thenReturn(true);
        boolean shouldNotFilter = oncePerRequestFilterBase.shouldNotFilter(request);
        assertTrue(shouldNotFilter);
        verify(antPathMatcher, times(2)).match(any(), eq(excludeUrl2));
    }
}