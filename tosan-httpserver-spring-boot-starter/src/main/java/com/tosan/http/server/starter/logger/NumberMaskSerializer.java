package com.tosan.http.server.starter.logger;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public class NumberMaskSerializer extends ValueSerializer<Number> {

    private SerializerUtility serializerUtility;

    public NumberMaskSerializer(SerializerUtility serializerUtility) {
        this.serializerUtility = serializerUtility;
    }

    public void serialize(Number value, JsonGenerator jsonGenerator, SerializationContext serializers)
            throws JacksonException {
        serializerUtility.serialize(value.toString(), jsonGenerator);
    }
}
