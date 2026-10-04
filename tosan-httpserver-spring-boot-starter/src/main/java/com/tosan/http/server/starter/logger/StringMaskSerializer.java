package com.tosan.http.server.starter.logger;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

/**
 * @author mina khoshnevisan
 * @since 7/31/2022
 */
public class StringMaskSerializer extends ValueSerializer<String> {

    private final SerializerUtility serializerUtility;

    public StringMaskSerializer(SerializerUtility serializerUtility) {
        this.serializerUtility = serializerUtility;
    }

    public void serialize(String value, JsonGenerator jsonGenerator, SerializationContext serializers)
            throws JacksonException {
        serializerUtility.serialize(value, jsonGenerator);
    }
}
