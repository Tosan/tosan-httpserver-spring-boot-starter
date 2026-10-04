package com.tosan.http.server.starter.logger;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

import java.util.Date;

public class DateSerializer extends ValueSerializer<Date> {

    private final SerializerUtility serializerUtility;

    public DateSerializer(SerializerUtility serializerUtility) {
        this.serializerUtility = serializerUtility;
    }

    @Override
    public void serialize(Date value, JsonGenerator jsonGenerator, SerializationContext serializers)
            throws JacksonException {
        serializerUtility.serialize(value.toString(), jsonGenerator);
    }
}
