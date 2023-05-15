package com.tosan.http.server.starter.logger;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.tosan.tools.mask.starter.replace.JsonReplaceHelperDecider;

import java.io.IOException;

public class SerializerUtility {
    private final JsonReplaceHelperDecider jsonReplaceHelperDecider;

    public SerializerUtility(JsonReplaceHelperDecider jsonReplaceHelperDecider) {
        this.jsonReplaceHelperDecider = jsonReplaceHelperDecider;
    }

    public void serialize(String value, JsonGenerator jsonGenerator) throws IOException {
        String fieldName = jsonGenerator.getOutputContext().getCurrentName();
        if (fieldName == null) {
            JsonStreamContext parent = jsonGenerator.getOutputContext().getParent();
            if (parent != null) {
                fieldName = parent.getCurrentName();
            }
        }
        if (value == null) {
            return;
        }
        String maskedValue = jsonReplaceHelperDecider.replace(fieldName, value);
        jsonGenerator.writeString(maskedValue);
    }
}