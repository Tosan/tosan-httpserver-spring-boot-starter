package com.tosan.http.server.starter.filter;

import com.tosan.http.server.starter.util.MdcUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;

import java.io.IOException;

/**
 * this filter set intended header parameters in MDC.
 * some parameters like X_REQUEST_ID can be generated randomly if not sent in request header.
 * specific characters defined in UnfreeCharacters will be replaced before putting in MDC
 *
 * @author mina khoshnevisan
 * @since 7/16/2022
 */
@Order(-300)
public class HttpMdcFilter extends OncePerRequestFilterBase {

    private final MdcUtil mdcLogUtil;

    public HttpMdcFilter(MdcUtil mdcLogUtil) {
        this.mdcLogUtil = mdcLogUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            mdcLogUtil.fillRemoteClientIp(request);
            mdcLogUtil.extractHeaderMdcParameters(request);
            filterChain.doFilter(request, response);
        } finally {
            mdcLogUtil.clear();
        }
    }
}