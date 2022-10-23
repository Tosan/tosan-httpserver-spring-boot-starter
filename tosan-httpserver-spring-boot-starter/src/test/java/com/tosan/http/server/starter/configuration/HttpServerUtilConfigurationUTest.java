package com.tosan.http.server.starter.configuration;

import com.tosan.http.server.starter.aspect.StatisticsAspect;
import com.tosan.http.server.starter.config.MdcFilterConfig;
import com.tosan.http.server.starter.filter.HttpLoggingFilter;
import com.tosan.http.server.starter.filter.HttpMdcFilter;
import com.tosan.http.server.starter.filter.HttpStatisticsFilter;
import com.tosan.http.server.starter.util.Constants;
import com.tosan.http.server.starter.util.HttpLogUtil;
import com.tosan.http.server.starter.util.MdcUtil;
import com.tosan.http.server.starter.util.StatisticsUtil;
import com.tosan.tools.mask.starter.config.SecureParametersConfig;
import com.tosan.tools.mask.starter.replace.JsonReplaceHelperDecider;
import org.junit.jupiter.api.Test;
import org.springframework.util.AntPathMatcher;

import static com.tosan.http.server.starter.util.Constants.AUTHORIZATION_SECURE_PARAM;
import static com.tosan.http.server.starter.util.Constants.PROXY_AUTHORIZATION_SECURE_PARAM;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author mina khoshnevisan
 * @since 7/17/2022
 */
public class HttpServerUtilConfigurationUTest {

    private HttpServerUtilConfiguration httpServerUtilConfiguration = new HttpServerUtilConfiguration();

    @Test
    public void testAntPathMatcher_normalCall_createBeanCorrectly() {
        AntPathMatcher antPathMatcher = httpServerUtilConfiguration.antPathMatcher();
        assertNotNull(antPathMatcher);
    }

    @Test
    public void testHttpLoggingFilter_normalCall_createBeanCorrectly() {
        HttpLoggingFilter httpLoggingFilter = httpServerUtilConfiguration.httpLoggingFilter(null);
        assertNotNull(httpLoggingFilter);
    }

    @Test
    public void testHttpMdcFilter_normalCall_createBeanCorrectly() {
        HttpMdcFilter httpMdcFilter = httpServerUtilConfiguration.httpMdcFilter(null);
        assertNotNull(httpMdcFilter);
    }

    @Test
    public void testHttpStatisticsFilter_normalCall_createBeanCorrectly() {
        HttpStatisticsFilter httpStatisticsFilter = httpServerUtilConfiguration.httpStatisticsFilter();
        assertNotNull(httpStatisticsFilter);
    }

    @Test
    public void testMdcUtil_normalCall_createBeanCorrectly() {
        MdcUtil mdcUtil = httpServerUtilConfiguration.mdcUtil(null);
        assertNotNull(mdcUtil);
    }

    @Test
    public void testHttpLogUtil_normalCall_createBeanCorrectly() {
        HttpLogUtil httpLogUtil = httpServerUtilConfiguration.httpLogUtil(null);
        assertNotNull(httpLogUtil);
    }

    @Test
    public void testJsonReplaceHelperDecider_normalCall_createBeanCorrectly() {
        JsonReplaceHelperDecider replaceHelperDecider = httpServerUtilConfiguration.
                replaceHelperDecider(null, null, null);
        assertNotNull(replaceHelperDecider);
    }

    @Test
    public void testSecureParametersConfig_normalCall_createBeanCorrectly() {
        SecureParametersConfig secureParametersConfig = httpServerUtilConfiguration.secureParametersConfig();
        assertNotNull(secureParametersConfig);
        assertEquals(AUTHORIZATION_SECURE_PARAM, secureParametersConfig.getSecuredParametersMap().get("authorization"));
        assertEquals(PROXY_AUTHORIZATION_SECURE_PARAM, secureParametersConfig.getSecuredParametersMap().get("proxy-authorization"));
    }

    @Test
    public void testMdcFilterConfig_normalCall_createCorrectBean() {
        MdcFilterConfig mdcFilterConfig = httpServerUtilConfiguration.mdcFilterConfig();
        assertNotNull(mdcFilterConfig);
        assertEquals(Constants.X_REQUEST_ID_MDC_PARAM, mdcFilterConfig.getParameters().get(0));
    }

    @Test
    public void testStatisticsAspect_normalCall_createCorrectBean() {
        StatisticsUtil statisticsUtil = new StatisticsUtil();
        StatisticsAspect statisticsAspect = httpServerUtilConfiguration.statisticsAspect(statisticsUtil);
        assertNotNull(statisticsAspect);
    }

    @Test
    public void testStatisticsUtil_normalCall_createCorrectBean() {
        StatisticsUtil statisticsUtil = httpServerUtilConfiguration.statisticsUtil();
        assertNotNull(statisticsUtil);
    }
}