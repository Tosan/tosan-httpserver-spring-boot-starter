package com.tosan.http.server.starter.config;

import java.util.List;

/**
 * @author mina khoshnevisan
 * @since 7/16/2022
 */
public class MdcFilterConfig {
    private List<HttpHeaderMdcParameter> parameters;
    private char[] unFreeChars = {'/', '\\', ':', '*', '?', '"', '<', '>', '|'};
    private char newChar = '-';

    public MdcFilterConfig(List<HttpHeaderMdcParameter> parameters) {
        this.parameters = parameters;
    }

    public MdcFilterConfig(List<HttpHeaderMdcParameter> parameters, char[] unFreeChars, char newChar) {
        this.parameters = parameters;
        this.unFreeChars = unFreeChars;
        this.newChar = newChar;
    }

    public List<HttpHeaderMdcParameter> getParameters() {
        return parameters;
    }

    public char[] getUnFreeChars() {
        return unFreeChars;
    }

    public char getNewChar() {
        return newChar;
    }
}