package com.tosan.http.server.starter.util;

import com.tosan.http.server.starter.wrapper.LogContentContainer;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author AmirHossein ZamanZade
 * @since 10/29/2022
 */
public class JsonHttpLogContentProvider extends LogContentProvider {

    @Override
    protected String generateRequestLogContent(LogContentContainer container) {
        Map<String, Object> requestContent = new LinkedHashMap<>(1);
        Map<String, Object> objectMap = new LinkedHashMap<>();
        objectMap.put("service", container.getUrl());
        objectMap.putAll(container.getHeaders());
        if (container.hasError()) {
            objectMap.putAll(container.getErrorParam());
        } else {
            if (container.isFormBody()) {
                objectMap.put("form parameters", container.getBody());
            } else {
                objectMap.put("body", container.getBody());
            }
        }
        requestContent.put("Http Request", objectMap);
        return ToStringJsonUtil.toJson(requestContent);
    }

    @Override
    protected String generateResponseLogContent(LogContentContainer container) {
        Map<String, Object> responseContent = new LinkedHashMap<>(1);
        Map<String, Object> objectMap = new LinkedHashMap<>();
        objectMap.put("status", container.getStatus());
        objectMap.putAll(container.getHeaders());
        if (container.hasError()) {
            objectMap.putAll(container.getErrorParam());
        } else {
            objectMap.put("body", container.getBody());
        }
        responseContent.put("Http Response", objectMap);
        return ToStringJsonUtil.toJson(responseContent);
    }
}
