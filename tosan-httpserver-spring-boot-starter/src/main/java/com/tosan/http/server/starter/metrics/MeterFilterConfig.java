package com.tosan.http.server.starter.metrics;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Shahryar Safizadeh
 * @since 2/17/2024
 */
@ConfigurationProperties(prefix = "metric.filter")
@Validated
public class MeterFilterConfig {

    private String[] excludedMeterNames;
    private Map<String, String> excludedMeterTags;
    private List<FilterRule> include = new ArrayList<>();

    public String[] getExcludedMeterNames() {
        return excludedMeterNames;
    }

    public void setExcludedMeterNames(String[] excludedMeterNames) {
        this.excludedMeterNames = excludedMeterNames;
    }

    public Map<String, String> getExcludedMeterTags() {
        return excludedMeterTags;
    }

    public void setExcludedMeterTags(Map<String, String> excludedMeterTags) {
        this.excludedMeterTags = excludedMeterTags;
    }

    public List<FilterRule> getInclude() {
        return include;
    }

    public void setInclude(List<FilterRule> include) {
        this.include = include;
    }
}