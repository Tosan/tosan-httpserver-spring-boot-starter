package com.tosan.http.server.sample;

import com.tosan.http.server.sample.mask.UserMaskType;
import com.tosan.http.server.starter.config.HttpHeaderMdcParameter;
import com.tosan.http.server.starter.config.MdcFilterConfig;
import com.tosan.http.server.starter.config.RandomGenerationType;
import com.tosan.http.server.starter.config.RandomParameter;
import com.tosan.http.server.starter.filter.HttpLoggingFilter;
import com.tosan.http.server.starter.filter.HttpMdcFilter;
import com.tosan.http.server.starter.filter.HttpStatisticsFilter;
import com.tosan.http.server.starter.util.HttpLogUtil;
import com.tosan.http.server.starter.util.MdcUtil;
import com.tosan.tools.mask.starter.business.enumeration.ComparisonType;
import com.tosan.tools.mask.starter.config.SecureParameter;
import com.tosan.tools.mask.starter.config.SecureParametersConfig;
import com.tosan.tools.mask.starter.configuration.MaskBeanConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.tosan.http.server.starter.util.Constants.*;

/**
 * @author mina khoshnevisan
 * @since 7/13/2022
 */
@Configuration
public class TestConfiguration {
    public static final String REQUEST_ID_PREFIX = "MCS-";

    @Bean("http-server-util-secured-parameters")
    public SecureParametersConfig secureParametersConfig() {
        Set<SecureParameter> securedParameters = MaskBeanConfiguration.SECURED_PARAMETERS;
        securedParameters.add(AUTHORIZATION_SECURE_PARAM);
        securedParameters.add(PROXY_AUTHORIZATION_SECURE_PARAM);
        securedParameters.add(new SecureParameter("secretKey", UserMaskType.RIGHT));
        securedParameters.add(new SecureParameter("test", UserMaskType.TEST_MASK_TYPE));
        securedParameters.add(new SecureParameter("username", UserMaskType.SEMI));
        securedParameters.add(new SecureParameter("mobile", UserMaskType.LEFT, ComparisonType.LEFT_LIKE));
        return new SecureParametersConfig(securedParameters);
    }

    @Bean("http-server-util-mdc-filter-config")
    public MdcFilterConfig mdcFilterConfig() {
        List<HttpHeaderMdcParameter> list = new ArrayList<>();
        HttpHeaderMdcParameter requestId = new HttpHeaderMdcParameter
                .HttpHeaderMdcParameterBuilder(X_REQUEST_ID, MDC_REQUEST_ID)
                .randomParameter(new RandomParameter(REQUEST_ID_PREFIX, RandomGenerationType.ALPHANUMERIC, 8))
                .build();
        HttpHeaderMdcParameter userIp = new HttpHeaderMdcParameter
                .HttpHeaderMdcParameterBuilder(X_USER_IP, MDC_USER_IP)
                .build();
        HttpHeaderMdcParameter freeUserIp = new HttpHeaderMdcParameter
                .HttpHeaderMdcParameterBuilder(X_USER_IP, MDC_USER_FREE_IP)
                .removeUnfreeCharacters(true)
                .build();
        list.add(requestId);
        list.add(userIp);
        list.add(freeUserIp);
        char[] unfreeCharacters = {'/', '\\', '*', '?', '"'};
        char newCharacter = '-';
        MdcFilterConfig mdcFilterConfig = new MdcFilterConfig(list, unfreeCharacters, newCharacter);
        return mdcFilterConfig;
    }

    @Bean
    public HttpStatisticsFilter httpStatisticsFilter() {
        HttpStatisticsFilter httpStatisticsFilter = new HttpStatisticsFilter();
        httpStatisticsFilter.addExcludeUrlPatterns(Collections.singletonList("/testUrl"));
        return httpStatisticsFilter;
    }

    @Bean
    public HttpLoggingFilter httpLoggingFilter(HttpLogUtil httpLogUtil) {
        HttpLoggingFilter httpLoggingFilter = new HttpLoggingFilter(httpLogUtil);
        List<String> excludeUrlPatterns = Collections.singletonList("/testUrl");
        httpLoggingFilter.addExcludeUrlPatterns(excludeUrlPatterns);
        return httpLoggingFilter;
    }

    @Bean
    public HttpMdcFilter httpMdcFilter(MdcUtil mdcUtil) {
        HttpMdcFilter httpMdcFilter = new HttpMdcFilter(mdcUtil);
        List<String> excludeUrlPatterns = Collections.singletonList("/testUrl");
        httpMdcFilter.setExcludeUrlPatterns(excludeUrlPatterns);
        return httpMdcFilter;
    }
}