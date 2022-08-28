package com.tosan.http.server.starter.config;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mina khoshnevisan
 * @since 8/27/2022
 */
public class ServiceLoggingConfig {

    List<Class<?>> ignoredParameterTypes = new ArrayList<>();

    public List<Class<?>> getIgnoredParameterTypes() {
        return ignoredParameterTypes;
    }

    public void setIgnoredParameterTypes(List<Class<?>> ignoredParameterTypes) {
        this.ignoredParameterTypes = ignoredParameterTypes;
    }
}