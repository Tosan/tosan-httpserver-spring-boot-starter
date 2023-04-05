package com.tosan.http.server.starter.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.tosan.http.server.starter.logger.FieldMaskBaseSerializer;
import com.tosan.tools.mask.starter.replace.JsonReplaceHelperDecider;

import java.text.SimpleDateFormat;

/**
 * @author Mostafa Abdollahi
 * @since 6/10/2021
 */
public class ToStringJsonUtil {
    private final ObjectMapper mapper = new ObjectMapper();
    private final JsonReplaceHelperDecider jsonReplaceHelperDecider;

    public ToStringJsonUtil(JsonReplaceHelperDecider jsonReplaceHelperDecider) {
        this.jsonReplaceHelperDecider = jsonReplaceHelperDecider;
        customizeObjectMapper();
    }

    private void customizeObjectMapper() {
        mapper.enable(SerializationFeature.INDENT_OUTPUT)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SimpleModule module = new SimpleModule();
        module.addSerializer(String.class, new FieldMaskBaseSerializer(jsonReplaceHelperDecider));
        mapper.registerModule(module);
        mapper.findAndRegisterModules();
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'H:m:ssZ"));
    }

    public <T> String toJson(T object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return "error creating json. " + e.getMessage();
        }
    }
}