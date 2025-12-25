package com.tosan.http.server.starter.metrics;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Elahe Hajizade
 * @since 25/12/2025
 */
public class FilterRule {
    private String name;
    private Map<String, String> tags = new HashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }
}