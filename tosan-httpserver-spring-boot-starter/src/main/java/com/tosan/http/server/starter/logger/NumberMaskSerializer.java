package com.tosan.http.server.starter.logger;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class NumberMaskSerializer extends JsonSerializer<Number> {

    private SerializerUtility serializerUtility;

    public NumberMaskSerializer(SerializerUtility serializerUtility) {
        this.serializerUtility = serializerUtility;
    }

    public void serialize(Number value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        serializerUtility.serialize(value.toString(), jsonGenerator);
    }
}