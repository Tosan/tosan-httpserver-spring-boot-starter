package com.tosan.http.server.starter.util;

import com.tosan.http.server.starter.config.HttpHeaderMdcParameter;
import com.tosan.http.server.starter.config.RandomGenerationType;
import com.tosan.tools.mask.starter.business.enumeration.MaskType;
import com.tosan.tools.mask.starter.config.SecureParameter;
import org.junit.jupiter.api.Test;

import static com.tosan.http.server.starter.util.Constants.X_REQUEST_ID;
import static com.tosan.http.server.starter.util.Constants.X_REQUEST_ID_PARAMETER_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author mina khoshnevisan
 * @since 7/17/2022
 */
public class ConstantsUTest {

    @Test
    public void checkDefaultActuatorExcludePattern() {
        String excludePattern = Constants.DEFAULT_ACTUATOR_EXCLUDE_PATTERN;
        assertEquals("/actuator/**", excludePattern);
    }

    @Test
    public void checkRemoteClientIpParameter() {
        String parameter = Constants.REMOTE_USER_IP_PARAMETER_NAME;
        assertEquals("userIP", parameter);
    }

    @Test
    public void checkRequestIdParameter() {
        String parameter = X_REQUEST_ID_PARAMETER_NAME;
        assertEquals("requestId", parameter);
    }

    @Test
    public void checkXRequestIdMdcParam() {
        HttpHeaderMdcParameter xRequestIdMdcParam = Constants.X_REQUEST_ID_MDC_PARAM;
        assertEquals(X_REQUEST_ID, xRequestIdMdcParam.getHeaderParameterName());
        assertEquals(X_REQUEST_ID_PARAMETER_NAME, xRequestIdMdcParam.getMdcParametersName());
        assertEquals(RandomGenerationType.ALPHANUMERIC, xRequestIdMdcParam.getRandomParameter().getGenerationType());
        assertEquals(8, xRequestIdMdcParam.getRandomParameter().getLength());
    }

    @Test
    public void checkAuthorizationSecureParam() {
        SecureParameter authorizationSecureParam = Constants.AUTHORIZATION_SECURE_PARAM;
        assertEquals("authorization", authorizationSecureParam.getParameterName());
        assertEquals(MaskType.COMPLETE, authorizationSecureParam.getMaskType());
    }

    @Test
    public void checkProxyAuthorizationSecureParam() {
        SecureParameter proxyAuthorizationSecureParam = Constants.PROXY_AUTHORIZATION_SECURE_PARAM;
        assertEquals("proxy-authorization", proxyAuthorizationSecureParam.getParameterName());
        assertEquals(MaskType.COMPLETE, proxyAuthorizationSecureParam.getMaskType());
    }
}