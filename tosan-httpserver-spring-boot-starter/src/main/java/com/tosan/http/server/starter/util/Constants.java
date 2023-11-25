package com.tosan.http.server.starter.util;

import com.tosan.http.server.starter.config.HttpHeaderMdcParameter;
import com.tosan.http.server.starter.config.RandomGenerationType;
import com.tosan.http.server.starter.config.RandomParameter;
import com.tosan.tools.mask.starter.business.enumeration.MaskType;
import com.tosan.tools.mask.starter.config.SecureParameter;

/**
 * @author mina khoshnevisan
 * @since 7/16/2022
 */
public interface Constants {
    /**
     * standard header names
     */
    String X_REQUEST_ID = "X-Request-ID";
    String X_FORWARDED_FOR = "X-Forwarded-For";
    String X_USER_IP = "X-User-IP";
    String CONTENT_LANGUAGE = "Content-Language";

    /**
     * constant default filter exclude patterns
     */
    String DEFAULT_ACTUATOR_EXCLUDE_PATTERN = "/actuator/**";
    String DEFAULT_SWAGGER_EXCLUDE_PATTERN = "/swagger-ui/**";
    String DEFAULT_API_DOCS_EXCLUDE_PATTERN = "/api-docs/**";
    String DEFAULT_API_DOCS_V3_EXCLUDE_PATTERN = "/v3/api-docs/**";
    String DEFAULT_FAVICON_EXCLUDE_PATTERN = "/favicon.ico";

    /**
     * constant mdc param names
     */
    String MDC_REQUEST_ID = "requestId";
    String MDC_USER_IP = "userIP";
    String MDC_USER_FREE_IP = "user-IP";
    String MDC_CLIENT_IP = "clientIP";
    String MDC_CLIENT_FREE_IP = "client-IP";

    /**
     * constant header mdc parameters
     */
    HttpHeaderMdcParameter X_REQUEST_ID_MDC_PARAM = new HttpHeaderMdcParameter.
            HttpHeaderMdcParameterBuilder(X_REQUEST_ID, MDC_REQUEST_ID).randomParameter(
            new RandomParameter(RandomGenerationType.ALPHANUMERIC, 8)).build();

    /**
     * secured header constants
     */
    SecureParameter AUTHORIZATION_SECURE_PARAM = new SecureParameter("authorization", MaskType.COMPLETE);
    SecureParameter PROXY_AUTHORIZATION_SECURE_PARAM = new SecureParameter("proxy-authorization", MaskType.COMPLETE);
}