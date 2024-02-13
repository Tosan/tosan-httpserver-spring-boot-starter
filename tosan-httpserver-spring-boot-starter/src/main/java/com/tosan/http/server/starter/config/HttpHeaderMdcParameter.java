package com.tosan.http.server.starter.config;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mina khoshnevisan
 * @since 7/16/2022
 */
public class HttpHeaderMdcParameter {
    private String headerParameterName;
    private String mdcParametersName;
    private boolean replaceUnfreeCharacters;
    private RandomParameter randomParameter;

    private HttpHeaderMdcParameter() {
    }

    public String getHeaderParameterName() {
        return headerParameterName;
    }

    public void setHeaderParameterName(String headerParameterName) {
        this.headerParameterName = headerParameterName;
    }

    public String getMdcParametersName() {
        return mdcParametersName;
    }

    public void setMdcParametersName(String mdcParametersName) {
        this.mdcParametersName = mdcParametersName;
    }

    public boolean isReplaceUnfreeCharacters() {
        return replaceUnfreeCharacters;
    }

    public void setReplaceUnfreeCharacters(boolean replaceUnfreeCharacters) {
        this.replaceUnfreeCharacters = replaceUnfreeCharacters;
    }

    public RandomParameter getRandomParameter() {
        return randomParameter;
    }

    public void setRandomParameter(RandomParameter randomParameter) {
        this.randomParameter = randomParameter;
    }

    public static class HttpHeaderMdcParameterBuilder {
        private static final Logger LOGGER = LoggerFactory.getLogger(HttpHeaderMdcParameterBuilder.class);
        private final String headerParameterName;
        private final String mdcParametersName;
        private boolean removeUnfreeCharacters;
        private RandomParameter randomParameter;

        public HttpHeaderMdcParameterBuilder(String headerParameterName, String mdcParametersName) {
            this.headerParameterName = headerParameterName;
            this.mdcParametersName = mdcParametersName;
        }

        public HttpHeaderMdcParameterBuilder removeUnfreeCharacters(boolean removeUnfreeCharacters) {
            this.removeUnfreeCharacters = removeUnfreeCharacters;
            return this;
        }

        public HttpHeaderMdcParameterBuilder randomParameter(RandomParameter randomParameter) {
            this.randomParameter = randomParameter;
            return this;
        }

        public HttpHeaderMdcParameter build() {
            validateParameters();
            HttpHeaderMdcParameter httpHeaderMdcParameter = new HttpHeaderMdcParameter();
            httpHeaderMdcParameter.setHeaderParameterName(this.headerParameterName);
            httpHeaderMdcParameter.setMdcParametersName(this.mdcParametersName);
            httpHeaderMdcParameter.setReplaceUnfreeCharacters(this.removeUnfreeCharacters);
            httpHeaderMdcParameter.setRandomParameter(this.randomParameter);
            return httpHeaderMdcParameter;
        }

        private void validateParameters() {
            if (StringUtils.isEmpty(this.headerParameterName)) {
                LOGGER.warn("invalid header parameter name:{}", headerParameterName);
            }
            if (StringUtils.isEmpty(this.mdcParametersName)) {
                LOGGER.warn("invalid header parameter name:{}", mdcParametersName);
            }
            if (this.randomParameter != null && this.randomParameter.getGenerationType() == null) {
                LOGGER.warn("invalid mdc random generation type:{}", this.randomParameter.getGenerationType());
            }
        }
    }
}