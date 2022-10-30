package com.tosan.http.server.starter.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author AmirHossein ZamanZade
 * @since 10/29/2022
 */
public class JsonHttpLogContentProvider extends LogContentProvider<LinkedHashMap<String, Object>> {

    @Override
    public LinkedHashMap<String, Object> getContentContainer() {
        return new LinkedHashMap<>();
    }

    @Override
    public void addToContent(String key, Object value, LinkedHashMap<String, Object> container) {
        container.put(key, value);
    }

    @Override
    public String generateLogContent(String key, LinkedHashMap<String, Object> container) {
        Map<String, Object> finalContent = new LinkedHashMap<>(1);
        finalContent.put(key, container);
        return ToStringJsonUtil.toJson(finalContent);
    }
}
