package com.tosan.http.server.starter.logger;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.tosan.tools.mask.starter.replace.JsonReplaceHelperDecider;

import java.io.IOException;

/**
 * @author mina khoshnevisan
 * @since 7/31/2022
 */
public class FieldMaskBaseSerializer extends JsonSerializer<String> {

    private final JsonReplaceHelperDecider jsonReplaceHelperDecider;

    public FieldMaskBaseSerializer(JsonReplaceHelperDecider jsonReplaceHelperDecider) {
        this.jsonReplaceHelperDecider = jsonReplaceHelperDecider;
    }

    public void serialize(String value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {

        String fieldName = jsonGenerator.getOutputContext().getCurrentName();
        if (value == null) {
            return;
        }
        String maskedValue = jsonReplaceHelperDecider.replace(fieldName, value);
        jsonGenerator.writeString(maskedValue);
    }
}