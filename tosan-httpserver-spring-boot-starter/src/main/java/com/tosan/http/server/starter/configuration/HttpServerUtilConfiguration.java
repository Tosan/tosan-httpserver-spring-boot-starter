package com.tosan.http.server.starter.configuration;

import com.tosan.http.server.starter.aspect.ServiceLogAspect;
import com.tosan.http.server.starter.aspect.TimerStatisticsAspect;
import com.tosan.http.server.starter.config.HttpHeaderMdcParameter;
import com.tosan.http.server.starter.config.MdcFilterConfig;
import com.tosan.http.server.starter.config.ServiceLoggingConfig;
import com.tosan.http.server.starter.filter.HttpLoggingFilter;
import com.tosan.http.server.starter.filter.HttpMdcFilter;
import com.tosan.http.server.starter.filter.HttpStatisticsFilter;
import com.tosan.http.server.starter.logger.JsonServiceLogger;
import com.tosan.http.server.starter.metrics.MeterFilterConfig;
import com.tosan.http.server.starter.metrics.MetricFilter;
import com.tosan.http.server.starter.metrics.util.MeterUtil;
import com.tosan.http.server.starter.util.*;
import com.tosan.tools.mask.starter.config.SecureParameter;
import com.tosan.tools.mask.starter.config.SecureParametersConfig;
import com.tosan.tools.mask.starter.replace.JacksonReplaceHelper;
import com.tosan.tools.mask.starter.replace.JsonReplaceHelperDecider;
import com.tosan.tools.mask.starter.replace.RegexReplaceHelper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.tosan.http.server.starter.util.Constants.AUTHORIZATION_SECURE_PARAM;
import static com.tosan.http.server.starter.util.Constants.PROXY_AUTHORIZATION_SECURE_PARAM;

/**
 * @author mina khoshnevisan
 * @since 7/12/2022
 */
@Configuration
public class HttpServerUtilConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AntPathMatcher antPathMatcher() {
        return new AntPathMatcher();
    }

    @Bean
    @ConditionalOnMissingBean
    public HttpLoggingFilter httpLoggingFilter(HttpLogUtil httpLogUtil) {
        return new HttpLoggingFilter(httpLogUtil);
    }

    @Bean
    @ConditionalOnMissingBean
    public HttpMdcFilter httpMdcFilter(MdcUtil mdcUtil) {
        return new HttpMdcFilter(mdcUtil);
    }

    @Bean
    @ConditionalOnMissingBean
    public HttpStatisticsFilter httpStatisticsFilter() {
        return new HttpStatisticsFilter();
    }

    @Bean
    @ConditionalOnMissingBean
    public MdcUtil mdcUtil(@Qualifier("http-server-util-mdc-filter-config") MdcFilterConfig mdcFilterConfig) {
        return new MdcUtil(mdcFilterConfig);
    }

    @Bean
    @ConditionalOnMissingBean
    public HttpLogUtil httpLogUtil(@Qualifier("http-server-util-regex-replace-helper") JsonReplaceHelperDecider replaceHelperDecider,
                                   LogContentProvider logContentProvider) {
        return new HttpLogUtil(replaceHelperDecider, logContentProvider);
    }

    @Bean("http-server-util-regex-replace-helper")
    @ConditionalOnMissingBean(name = "http-server-util-regex-replace-helper")
    public JsonReplaceHelperDecider replaceHelperDecider(
            JacksonReplaceHelper jacksonReplaceHelper,
            RegexReplaceHelper regexReplaceHelper,
            @Qualifier("http-server-util-secured-parameters") SecureParametersConfig secureParametersConfig) {
        return new JsonReplaceHelperDecider(jacksonReplaceHelper, regexReplaceHelper, secureParametersConfig);
    }

    @Bean("http-server-util-secured-parameters")
    @ConditionalOnMissingBean(name = "http-server-util-secured-parameters")
    public SecureParametersConfig secureParametersConfig() {
        HashSet<SecureParameter> securedParameters = new HashSet<>();
        securedParameters.add(AUTHORIZATION_SECURE_PARAM);
        securedParameters.add(PROXY_AUTHORIZATION_SECURE_PARAM);
        return new SecureParametersConfig(securedParameters);
    }

    @Bean("http-server-util-mdc-filter-config")
    @ConditionalOnMissingBean(name = "http-server-util-mdc-filter-config")
    public MdcFilterConfig mdcFilterConfig() {
        List<HttpHeaderMdcParameter> list = new ArrayList<>();
        list.add(Constants.X_REQUEST_ID_MDC_PARAM);
        return new MdcFilterConfig(list);
    }

    @Bean
    @ConditionalOnProperty(value = "serviceLog.enabled", matchIfMissing = true)
    public ServiceLogAspect serviceLogAspect(JsonServiceLogger jsonServiceLogger) {
        return new ServiceLogAspect(jsonServiceLogger);
    }

    @Bean
    public JsonServiceLogger jsonServiceLogger(ServiceLoggingConfig serviceLoggingConfig, ToStringJsonUtil toStringJsonUtil) {
        return new JsonServiceLogger(serviceLoggingConfig, toStringJsonUtil);
    }

    @Bean
    @ConditionalOnMissingBean
    public ServiceLoggingConfig serviceLoggingConfig() {
        ServiceLoggingConfig serviceLoggingConfig = new ServiceLoggingConfig();
        List<Class<?>> ignoredParameterTypes = new ArrayList<>();
        ignoredParameterTypes.add(HttpServletRequest.class);
        ignoredParameterTypes.add(HttpServletResponse.class);
        ignoredParameterTypes.add(BindingResult.class);
        serviceLoggingConfig.setIgnoredParameterTypes(ignoredParameterTypes);
        return serviceLoggingConfig;
    }

    @Bean
    public TimerStatisticsAspect statisticsAspect(TimerStatisticsUtil timerStatisticsUtil, AspectUtil aspectUtil) {
        return new TimerStatisticsAspect(timerStatisticsUtil, aspectUtil);
    }

    @Bean
    public TimerStatisticsUtil statisticsUtil() {
        return new TimerStatisticsUtil();
    }

    @Bean
    public AspectUtil aspectUtil() {
        return new AspectUtil();
    }

    @Bean
    @ConditionalOnProperty(value = "http.log.format", havingValue = "json")
    public LogContentProvider jsonHttpLogContentProvider() {
        return new JsonHttpLogContentProvider();
    }

    @Bean
    @ConditionalOnProperty(value = "http.log.format", havingValue = "raw", matchIfMissing = true)
    public LogContentProvider rawHttpLogContentProvider() {
        return new RawHttpLogContentProvider();
    }

    @Bean
    public ToStringJsonUtil toStringJsonUtil(@Qualifier("http-server-util-regex-replace-helper")
                                                         JsonReplaceHelperDecider jsonReplaceHelperDecider) {
        return new ToStringJsonUtil(jsonReplaceHelperDecider);
    }

    @Bean
    @ConditionalOnMissingBean
    public MeterUtil meterUtil() {
        return new MeterUtil();
    }

    @Bean
    @ConditionalOnMissingBean
    public MeterFilterConfig meterFilterConfig() {
        return new MeterFilterConfig();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = "metric.filter.enable", havingValue = "true")
    public MetricFilter metricFilter(MeterFilterConfig meterFilterConfig) {
        return new MetricFilter(meterFilterConfig);
    }
}