package com.tosan.http.server.starter.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.tosan.http.server.starter.logger.FieldMaskBaseSerializer;

import java.text.SimpleDateFormat;

/**
 * @author Mostafa Abdollahi
 * @since 6/10/2021
 */
public class ToStringJsonUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.enable(SerializationFeature.INDENT_OUTPUT)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SimpleModule module = new SimpleModule();
        module.addSerializer(String.class, new FieldMaskBaseSerializer());
        mapper.registerModule(module);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'H:m:ssZ"));
    }

    public static <T> String toJson(T object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return "error creating json. " + e.getMessage();
        }
    }
}