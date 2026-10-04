package com.tosan.http.server.starter.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tosan.http.server.starter.logger.*;
import com.tosan.tools.mask.starter.replace.JsonReplaceHelperDecider;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Mostafa Abdollahi
 * @since 6/10/2021
 */
public class ToStringJsonUtil {
    private final ObjectMapper mapper;
    private final JsonReplaceHelperDecider jsonReplaceHelperDecider;

    public ToStringJsonUtil(JsonReplaceHelperDecider jsonReplaceHelperDecider) {
        this.jsonReplaceHelperDecider = jsonReplaceHelperDecider;
        this.mapper = createObjectMapper();
    }

    private ObjectMapper createObjectMapper() {
        SimpleModule module = new SimpleModule();
        SerializerUtility serializerUtility = new SerializerUtility(jsonReplaceHelperDecider);
        StringMaskSerializer stringMaskSerializer = new StringMaskSerializer(serializerUtility);
        NumberMaskSerializer numberMaskSerializer = new NumberMaskSerializer(serializerUtility);
        MultipartMaskSerializer multipartMaskSerializer = new MultipartMaskSerializer(serializerUtility);
        module.addSerializer(String.class, stringMaskSerializer);
        module.addSerializer(Number.class, numberMaskSerializer);
        module.addSerializer(int.class, numberMaskSerializer);
        module.addSerializer(long.class, numberMaskSerializer);
        module.addSerializer(float.class, numberMaskSerializer);
        module.addSerializer(double.class, numberMaskSerializer);
        module.addSerializer(short.class, numberMaskSerializer);
        module.addSerializer(byte[].class, new ByteArraySerializer());
        DateSerializer dateSerializer = new DateSerializer(serializerUtility);
        module.addSerializer(Date.class, dateSerializer);
        module.addSerializer(MultipartFile.class, multipartMaskSerializer);
        return JsonMapper.builder()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .changeDefaultPropertyInclusion(incl ->
                        JsonInclude.Value.construct(JsonInclude.Include.NON_NULL, JsonInclude.Include.NON_NULL))
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModule(module)
                .findAndAddModules()
                .defaultDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'H:m:ssZ"))
                .build();
    }

    public <T> String toJson(T object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JacksonException e) {
            return "error creating json. " + e.getMessage();
        }
    }
}
