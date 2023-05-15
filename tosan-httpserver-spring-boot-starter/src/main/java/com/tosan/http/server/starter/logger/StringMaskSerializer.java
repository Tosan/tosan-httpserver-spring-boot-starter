package com.tosan.http.server.starter.logger;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * @author mina khoshnevisan
 * @since 7/31/2022
 */
public class StringMaskSerializer extends JsonSerializer<String> {

    private final SerializerUtility serializerUtility;

    public StringMaskSerializer(SerializerUtility serializerUtility) {
        this.serializerUtility = serializerUtility;
    }

    public void serialize(String value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        serializerUtility.serialize(value, jsonGenerator);
    }
}