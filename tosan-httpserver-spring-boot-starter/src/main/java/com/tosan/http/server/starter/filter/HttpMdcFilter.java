package com.tosan.http.server.starter.filter;

import com.tosan.http.server.starter.util.MdcUtil;
import org.springframework.core.annotation.Order;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * @author mina khoshnevisan
 * @since 7/16/2022
 *
 * this filter set intended header parameters in MDC.
 * some parameters like X_REQUEST_ID can be generated randomly if not sent in request header.
 * specific characters defined in UnfreeCharacters will be replaced before putting in MDC
 */
@Order(10)
public class HttpMdcFilter extends OncePerRequestFilterBase {

    private final MdcUtil mdcLogUtil;

    public HttpMdcFilter(MdcUtil mdcLogUtil) {
        this.mdcLogUtil = mdcLogUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            mdcLogUtil.fillRemoteClientIp();
            mdcLogUtil.extractHeaderMdcParameters(request);
            filterChain.doFilter(request, response);
        } finally {
            mdcLogUtil.clear();
        }
    }
}