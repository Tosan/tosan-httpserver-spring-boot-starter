package com.tosan.http.server.starter.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tosan.http.server.starter.wrapper.LogContentContainer;
import org.apache.commons.lang3.StringUtils;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author AmirHossein ZamanZade
 * @since 10/29/2022
 */
public class JsonHttpLogContentProvider extends LogContentProvider {

    private static final ObjectMapper MAPPER = JsonMapper.builder()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .changeDefaultPropertyInclusion(incl ->
                    JsonInclude.Value.construct(JsonInclude.Include.NON_NULL, JsonInclude.Include.NON_NULL))
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();

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
        if (StringUtils.isNotEmpty(container.getBody())) {
            objectMap.put("body", container.getBody());
        }
    }

    private String toJson(Object value) {
        try {
            return MAPPER.writeValueAsString(value);
        } catch (JacksonException e) {
            return "error creating json. " + e.getMessage();
        }
    }
}
