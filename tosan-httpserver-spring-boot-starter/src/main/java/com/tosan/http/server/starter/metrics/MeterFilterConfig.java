package com.tosan.http.server.starter.metrics;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

/**
 * @author Shahryar Safizadeh
 * @since 2/17/2024
 */
@ConfigurationProperties(prefix = "metric.filter")
@Validated
public class MeterFilterConfig {

    private String[] filteredMeterNames;
    private Map<String, String> filteredMeterTags;

    public String[] getFilteredMeterNames() {
        return filteredMeterNames;
    }

    public void setFilteredMeterNames(String[] filteredMeterNames) {
        this.filteredMeterNames = filteredMeterNames;
    }

    public Map<String, String> getFilteredMeterTags() {
        return filteredMeterTags;
    }

    public void setFilteredMeterTags(Map<String, String> filteredMeterTags) {
        this.filteredMeterTags = filteredMeterTags;
    }
}