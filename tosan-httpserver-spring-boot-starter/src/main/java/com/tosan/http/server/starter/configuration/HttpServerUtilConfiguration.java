package com.tosan.http.server.starter.configuration;

import com.tosan.http.server.starter.aspect.ServiceLogAspect;
import com.tosan.http.server.starter.config.HttpHeaderMdcParameter;
import com.tosan.http.server.starter.config.MdcFilterConfig;
import com.tosan.http.server.starter.filter.HttpLoggingFilter;
import com.tosan.http.server.starter.filter.HttpMdcFilter;
import com.tosan.http.server.starter.filter.HttpStatisticsFilter;
import com.tosan.http.server.starter.util.Constants;
import com.tosan.http.server.starter.util.HttpLogUtil;
import com.tosan.http.server.starter.util.MdcUtil;
import com.tosan.http.server.starter.util.ServiceLogUtil;
import com.tosan.tools.mask.starter.config.SecureParameter;
import com.tosan.tools.mask.starter.config.SecureParametersConfig;
import com.tosan.tools.mask.starter.replace.JacksonReplaceHelper;
import com.tosan.tools.mask.starter.replace.RegexReplaceHelper;
import com.tosan.tools.mask.starter.replace.JsonReplaceHelperDecider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;

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
    public HttpLogUtil httpLogUtil(@Qualifier("http-server-util-regex-replace-helper") JsonReplaceHelperDecider replaceHelperDecider) {
        return new HttpLogUtil(replaceHelperDecider);
    }

    @Bean("http-server-util-regex-replace-helper")
    @ConditionalOnMissingBean
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
    public ServiceLogAspect serviceLogAspect(ServiceLogUtil serviceLogUtil) {
        return new ServiceLogAspect(serviceLogUtil);
    }

    @Bean
    public ServiceLogUtil serviceLogUtil() {
        return new ServiceLogUtil();
    }
}