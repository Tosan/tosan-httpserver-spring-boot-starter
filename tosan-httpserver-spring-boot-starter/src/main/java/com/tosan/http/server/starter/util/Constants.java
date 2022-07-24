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
    String X_FORWARDED_FOR = "X_Forwarded_For";
    String X_USER_IP = "X_User_IP";

    /**
     * constant default filter exclude patterns
     */
    String DEFAULT_ACTUATOR_EXCLUDE_PATTERN = "/actuator/**";
    String DEFAULT_SWAGGER_EXCLUDE_PATTERN = "/swagger-ui/**";
    String DEFAULT_API_DOCS_EXCLUDE_PATTERN = "/api-docs/**";
    String DEFAULT_FAVICON_EXCLUDE_PATTERN = "/favicon.ico";

    /**
     * constant mdc param names
     */
    String REMOTE_USER_IP_PARAMETER_NAME = "userIP";
    String X_REQUEST_ID_PARAMETER_NAME = "requestId";
    String CLIENT_IP_PARAMETER_NAME = "clientIP";

    /**
     * constant header mdc parameters
     */
    HttpHeaderMdcParameter X_REQUEST_ID_MDC_PARAM = new HttpHeaderMdcParameter.
            HttpHeaderMdcParameterBuilder(X_REQUEST_ID, X_REQUEST_ID_PARAMETER_NAME).randomParameter(
            new RandomParameter(RandomGenerationType.ALPHANUMERIC, 8)).build();

    /**
     * secured header constants
     */
    SecureParameter AUTHORIZATION_SECURE_PARAM = new SecureParameter("authorization", MaskType.COMPLETE);
    SecureParameter PROXY_AUTHORIZATION_SECURE_PARAM = new SecureParameter("proxy-authorization", MaskType.COMPLETE);
}