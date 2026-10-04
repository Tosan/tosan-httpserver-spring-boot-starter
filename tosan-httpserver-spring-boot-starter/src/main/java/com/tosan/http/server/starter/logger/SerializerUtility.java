package com.tosan.http.server.starter.logger;

import com.tosan.tools.mask.starter.replace.JsonReplaceHelperDecider;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.TokenStreamContext;

public class SerializerUtility {
    private final JsonReplaceHelperDecider jsonReplaceHelperDecider;

    public SerializerUtility(JsonReplaceHelperDecider jsonReplaceHelperDecider) {
        this.jsonReplaceHelperDecider = jsonReplaceHelperDecider;
    }

    public void serialize(String value, JsonGenerator jsonGenerator) throws JacksonException {
        String fieldName = jsonGenerator.streamWriteContext().currentName();
        if (fieldName == null) {
            TokenStreamContext parent = jsonGenerator.streamWriteContext().getParent();
            if (parent != null) {
                fieldName = parent.currentName();
            }
        }
        if (value == null) {
            return;
        }
        String maskedValue = jsonReplaceHelperDecider.replace(fieldName, value);
        jsonGenerator.writeString(maskedValue);
    }
}
