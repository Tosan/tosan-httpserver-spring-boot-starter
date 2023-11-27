package com.tosan.http.server.starter.filter;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author M.khoshnevisan
 * @since 4/21/2021
 */
public abstract class OncePerRequestFilterBase extends OncePerRequestFilter {

    private AntPathMatcher antPathMatcher;
    private List<String> excludeUrlPatterns;

    @Autowired
    public void setAntPathMatcher(AntPathMatcher antPathMatcher) {
        this.antPathMatcher = antPathMatcher;
    }

    /**
     * this method add a new list of exclude url patterns
     *
     * @param excludeUrlPatterns exclude url pattern list
     */
    public void setExcludeUrlPatterns(List<String> excludeUrlPatterns) {
        this.excludeUrlPatterns = excludeUrlPatterns;
    }

    /**
     * this method add new exclude url patterns to default patterns
     *
     * @param excludeUrlPatterns exclude url pattern list
     */
    public void addExcludeUrlPatterns(List<String> excludeUrlPatterns) {
        if (this.excludeUrlPatterns == null) {
            this.excludeUrlPatterns = new ArrayList<>();
        }
        this.excludeUrlPatterns.addAll(excludeUrlPatterns);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (excludeUrlPatterns == null || excludeUrlPatterns.size() == 0) {
            return false;
        }
        return excludeUrlPatterns.stream()
                .anyMatch(excludeUrl -> antPathMatcher.match(excludeUrl, request.getServletPath()));
    }
}