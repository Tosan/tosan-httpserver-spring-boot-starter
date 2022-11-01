package com.tosan.http.server.starter.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.tosan.http.server.starter.wrapper.LogContentContainer;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author AmirHossein ZamanZade
 * @since 10/29/2022
 */
public class JsonHttpLogContentProvider extends LogContentProvider {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.enable(SerializationFeature.INDENT_OUTPUT)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    protected String generateRequestLogContent(LogContentContainer container) {
        Map<String, Object> requestContent = new LinkedHashMap<>(1);
        Map<String, Object> objectMap = new LinkedHashMap<>();
        objectMap.put("service", container.getUrl());
        addHeaders(container, objectMap);
        if (container.hasErrorInBodyRendering()) {
            objectMap.putAll(container.getErrorParam());
        } else {
            if (container.isFormBody()) {
                objectMap.put("form parameters", container.getBody());
            } else {
                addBody(container, objectMap);
            }
        }
        requestContent.put("Http Request", objectMap);
        return toJson(requestContent);
    }

    @Override
    protected String generateResponseLogContent(LogContentContainer container) {
        Map<String, Object> responseContent = new LinkedHashMap<>(1);
        Map<String, Object> objectMap = new LinkedHashMap<>();
        objectMap.put("status", container.getStatus());
        addHeaders(container, objectMap);
        if (container.hasErrorInBodyRendering()) {
            objectMap.putAll(container.getErrorParam());
        } else {
            addBody(container, objectMap);
        }
        responseContent.put("Http Response", objectMap);
        return toJson(responseContent);
    }

    private void addHeaders(LogContentContainer container, Map<String, Object> objectMap) {
        if (!container.getHeaders().isEmpty()) {
            objectMap.put("headers", container.getHeaders());
        }
    }

    private void addBody(LogContentContainer container, Map<String, Object> objectMap) {
        if (!StringUtils.isEmpty(container.getBody())) {
            objectMap.put("body", container.getBody());
        }
    }

    private String toJson(Object value) {
        try {
            return MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            return "error creating json. " + e.getMessage();
        }
    }
}
