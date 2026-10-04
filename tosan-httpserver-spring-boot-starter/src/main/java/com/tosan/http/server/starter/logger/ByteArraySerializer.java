package com.tosan.http.server.starter.logger;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

/**
 * @author AmirHossein ZamanZade
 * @since 1/23/2024
 */
public class ByteArraySerializer extends ValueSerializer<byte[]> {

    @Override
    public void serialize(byte[] value, JsonGenerator jsonGenerator, SerializationContext serializers)
            throws JacksonException {
        jsonGenerator.writeString("*MASKED with size = " + value.length);
    }
}
